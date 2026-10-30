package com.onthecrow.caffeine.data.permission

import kotlinx.coroutines.flow.Flow

interface PermissionManager {
    val isGranted: Flow<Boolean>
    fun request()
}
