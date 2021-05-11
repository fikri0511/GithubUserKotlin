package org.sonicboom.githubapplication.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.speech.RecognizerIntent
import android.text.TextUtils
import android.view.View
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.miguelcatalan.materialsearchview.MaterialSearchView
import kotlinx.android.synthetic.main.component_toolbar.*
import kotlinx.android.synthetic.main.item_bottomsheet_detail_user.*
import kotlinx.android.synthetic.main.item_list_users.view.*
import org.sonicboom.githubapplication.R
import org.sonicboom.githubapplication.constant.ApiStatus
import org.sonicboom.githubapplication.databinding.ActivityMainBinding
import org.sonicboom.githubapplication.model.UserItem
import org.sonicboom.githubapplication.utils.PagedAdapterUtil
import org.sonicboom.githubapplication.utils.stateUtilWithEmptyView
import kotlin.random.Random


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private val viewModel: MainActivityViewModel by lazy {
        ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        ).get(MainActivityViewModel::class.java)
    }
    private lateinit var usersAdapter: PagedAdapterUtil<UserItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Voice Search
        initialSearchUser()
        setupUserSearch()
    }

    private fun initialSearchUser() {
        val CONSONANT = arrayListOf(
            "B",
            "C",
            "D",
            "F",
            "G",
            "H",
            "J",
            "K",
            "L",
            "M",
            "N",
            "P",
            "Q",
            "R",
            "S",
            "T",
            "U",
            "V",
            "W",
            "X",
            "Y",
            "Z"
        )
        val VOCAL = arrayListOf("A", "I", "U", "E", "O")

        val randomVocal = Random.nextInt(0, VOCAL.size - 1)
        val randomConsonant = Random.nextInt(0, CONSONANT.size - 1)

        val mixedString = CONSONANT[randomConsonant] + VOCAL[randomVocal]

        searchUser(mixedString)
    }

    private fun searchUser(query: String) {
        viewModel.setUpListUsers(query)
        observeSearchUsers()
        searchView.setVoiceSearch(true)


        //Setup Adapter
        usersAdapter = PagedAdapterUtil(R.layout.item_list_users, { _, itemView, item ->
            itemView.tvName.text = item.login
            Glide.with(this).load(item.avatarUrl).circleCrop().into(itemView.imPhoto)

        }, { _, item ->
            bottomSheet(item)
            if (bottomSheet.state == BottomSheetBehavior.STATE_EXPANDED)
                bottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
            else
                bottomSheet.state = BottomSheetBehavior.STATE_EXPANDED

        })
        binding.rvUsersGithub.layoutManager = LinearLayoutManager(this)
        binding.rvUsersGithub.adapter = usersAdapter


    }

    private fun observeSearchUsers() {
        viewModel.getDataListGithubUsers().observe(this, {
            usersAdapter.submitList(it)
        })
        viewModel.statusGithubUsers.observe(this, {
            stateUtilWithEmptyView(
                this,
                it,
                binding.progressBarUsersList,
                binding.rvUsersGithub,
                binding.emptyView.tvEmptyView,
                binding.emptyView.imEmptyView,
                binding.emptyView.layoutEmptyView,
                "Data User Tidak Ditemukan",
                R.drawable.ic_search,
                binding.emptyView.lottie
            )
        })
    }

    //Search User
    private fun setupUserSearch() {
        binding.toolbarContainer.searchButton.setOnClickListener {
            binding.toolbarContainer.searchView.showSearch()
            searchView.setVoiceSearch(true)

        }
        binding.toolbarContainer.searchView.setOnQueryTextListener(object :
            MaterialSearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchUser(query!!)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                return true
            }

        })

        binding.toolbarContainer.searchView.setOnSearchViewListener(object :
            MaterialSearchView.SearchViewListener {
            override fun onSearchViewShown() {

            }

            override fun onSearchViewClosed() {

            }

        })


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == MaterialSearchView.REQUEST_VOICE && resultCode == RESULT_OK) {
            val matches = data!!.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (matches != null && matches.size > 0) {
                val searchWord = matches[0]
                if (!TextUtils.isEmpty(searchWord)) {
                    searchView.setQuery(searchWord, false)
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    //Bottom sheet

    //bottom sheetHandler
    private lateinit var bottomSheet: BottomSheetBehavior<RelativeLayout>

    private fun bottomSheet(user: UserItem) {
        bottomSheet = BottomSheetBehavior.from(bottomSheetDetail)
        bottomSheet.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {

            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }
        })
        viewModel.getDetailUser(user.login!!)
        viewModel.detailUser.observe(this, {
            tvJumlahFollowers.text = it.followers.toString()
            tvJumlahFollowing.text = it.following.toString()
            tvJumlahRepo.text = it.publicRepos.toString()
            tvNameDetail.text = it.login.toString()
            if (it.email != null) {
                tvEmail.text = it.email.toString()
            } else {
                tvEmail.visibility = View.GONE
            }
            if (it.bio != null) {
                tvIsiBio.text = it.bio.toString()
            } else {
                tvIsiBio.text = "-"
            }
            Glide.with(this).load(it.avatarUrl).into(imPhotoProfil)
        })

        btWeb.setOnClickListener {
            val tabsIntent = CustomTabsIntent.Builder().build()
            tabsIntent.launchUrl(this, Uri.parse(user.htmlUrl))
        }
        progressLoadData()


    }

    private fun progressLoadData() {
        viewModel.status.observe(this, {
            when (it) {
                ApiStatus.SUCCESS -> {
                    progressBarDetail.visibility = View.GONE
                    layoutNama.visibility = View.VISIBLE

                }
                ApiStatus.FAILED -> {
                    Toast.makeText(
                        this,
                        "Jaringan Bermasalah atau Request Limit",
                        Toast.LENGTH_SHORT
                    ).show()
                    progressBarDetail.visibility = View.GONE
                }
                ApiStatus.LOADING -> {
                    progressBarDetail.visibility = View.VISIBLE
                    layoutNama.visibility = View.GONE
                }
                else -> {
                    Toast.makeText(
                        this,
                        "Jaringan Bermasalah atau Request Limit",
                        Toast.LENGTH_SHORT
                    ).show()
                    progressBarDetail.visibility = View.GONE
                }

            }
        })
    }
}