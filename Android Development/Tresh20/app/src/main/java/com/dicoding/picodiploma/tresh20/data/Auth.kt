package com.dicoding.picodiploma.tresh20.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Auth(
    val token: String,
    val isLogin: Boolean
):Parcelable

