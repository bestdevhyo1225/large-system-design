package com.hyoseok.sns.repository

import com.hyoseok.sns.entity.Sns

interface SnsRepository {
    fun save(sns: Sns)
    fun update(sns: Sns)
    fun delete(id: Long)
}
