package com.example.notesforgoats

data class Note(
    val id: Int,
    val title: String,
    val content: String,
    val createdAt: Long = System.currentTimeMillis()
)