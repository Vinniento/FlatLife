package com.androiddevs.ktornoteapp.data.remote.requests

import fh.wfp2.flatlife.data.room.entities.Task

data class DeleteTaskRequest(
    val taskId: Long
)