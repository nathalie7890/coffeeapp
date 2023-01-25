package com.nathalie.coffeeapp.repository

import com.nathalie.coffeeapp.data.CoffeeDao
import com.nathalie.coffeeapp.data.model.Drink
import kotlinx.coroutines.flow.Flow

class DrinkRepository(private val coffeeDao: CoffeeDao) {

    //fetch drinks
    suspend fun getDrinks(str: String, cat: Int, fav: Boolean): List<Drink> {
        if (fav) {
            return coffeeDao.getDrinks().filter {
                Regex(
                    str,
                    RegexOption.IGNORE_CASE
                ).containsMatchIn(it.title) && it.favorite == true
            }.toList()
        }

        if (cat == 0) {
            return coffeeDao.getDrinks().filter {
                Regex(
                    str,
                    RegexOption.IGNORE_CASE
                ).containsMatchIn(it.title)
            }.toList()
        }

        return coffeeDao.getDrinks().filter {
            Regex(
                str,
                RegexOption.IGNORE_CASE
            ).containsMatchIn(it.title) && it.category == cat
        }.toList()
    }

    //add drink
    suspend fun addDrink(drink: Drink) {
        coffeeDao.insert(drink)
    }

    //find drink that match the given id
    fun getDrinkById(id: Long): Flow<Drink?> {
        return coffeeDao.getDrinkById(id)
    }


    //find drink by id and update it
    suspend fun updateDrink(id: Long, drink: Drink) {
        coffeeDao.insert(drink.copy(id = id))
    }

    suspend fun favDrink(id: Long, status: Boolean) {
        coffeeDao.favDrink(id, status)
    }

    //delete drink by id
    suspend fun deleteDrink(id: Long) {
        coffeeDao.delete(id)
    }

    //find drink by title
    suspend fun getDrinkByTitle(title: String): List<Drink> {
        return coffeeDao.getDrinkByTitle(title)
    }
}