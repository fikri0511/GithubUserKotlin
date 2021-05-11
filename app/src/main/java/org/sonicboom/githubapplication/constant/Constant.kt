package org.sonicboom.githubapplication.constant

var BASE_URL = "https://api.github.com/"
//New
//var APIKEY = "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"
//Old
var APIKEY = "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"
//status loading
enum class ApiStatus { EMPTY, EMPTY_BEFORE, EMPTY_AFTER, LOADING, LOADED, SUCCESS, FAILED ,FAILED_API}
