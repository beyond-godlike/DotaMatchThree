package com.example.dotamatchthree.data.api.dao

import com.example.dotamatchthree.data.Level
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test


class LevelDaoTest {

    private lateinit var dao: LevelDao

    @Before
    fun setup() {
        dao = mockk()
    }

    @Test
    fun testInsertLevels() {
        val levels = listOf(
            Level(1, 20, 2, 10),
            Level(2, 6, 4, 12)
        )

        every { dao.insertLevels(levels) } just runs

        dao.insertLevels(levels)

        verify { dao.insertLevels(levels) }
    }

    @Test
    fun testGetLevels() {
        val levels = listOf(
            Level(1, 20, 2, 10),
            Level(2, 6, 4, 12)
        )

        every { dao.getLevels() } returns levels

        val result = dao.getLevels()

        assertEquals(levels, result)

    }

    @Test
    fun testGetLevel() {
        val level = Level(1, 6, 2, 10)

        every { dao.getLevel(1) } returns level

        val result = dao.getLevel(1)

        assertEquals(level, result)
    }
}