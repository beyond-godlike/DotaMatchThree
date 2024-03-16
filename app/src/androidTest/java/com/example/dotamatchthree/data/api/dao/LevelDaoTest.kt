package com.example.dotamatchthree.data.api.dao

import com.example.dotamatchthree.data.Level
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner


@RunWith(MockitoJUnitRunner::class)
class LevelDaoTest {

    private lateinit var dao: LevelDao

    @Before
    fun setup() {
        dao = Mockito.mock(LevelDao::class.java)
    }

    @Test
    fun testInsertLevels() {
        val levels = listOf(
            Level(1, 20, 2, 10),
            Level(2, 6, 4, 12)
        )

        dao.insertLevels(levels)

        Mockito.verify(dao).insertLevels(levels)
    }

    @Test
    fun testGetLevels() {
        val levels = listOf(
            Level(1, 20, 2, 10),
            Level(2, 6, 4, 12)
        )

        Mockito.`when`(dao.getLevels()).thenReturn(levels)

        val retrievedLevels = dao.getLevels()

        assert(retrievedLevels.size == levels.size)
        assert(retrievedLevels.containsAll(levels))
    }

    @Test
    fun testGetLevel() {
        val level = Level(1, 6, 2, 10)

        Mockito.`when`(dao.getLevel(1)).thenReturn(level)

        val retrievedLevel = dao.getLevel(1)

        assert(retrievedLevel == level)
    }
}