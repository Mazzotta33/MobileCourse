package com.example.mycity.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.BakeryDining
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Castle
import androidx.compose.material.icons.filled.Chair
import androidx.compose.material.icons.filled.Cookie
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.EmojiFoodBeverage
import androidx.compose.material.icons.filled.Festival
import androidx.compose.material.icons.filled.Forest
import androidx.compose.material.icons.filled.Fort
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.Icecream
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.LunchDining
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.NaturePeople
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.TheaterComedy
import androidx.compose.material.icons.filled.Villa
import androidx.compose.material.icons.filled.Waves
import com.example.mycity.R
import com.example.mycity.model.Category
import com.example.mycity.model.Place

/**
 * Local source of the city recommendations
 */
object LocalPlacesDataProvider {

    val allPlaces: List<Place> = listOf(
        // Sights
        Place(
            id = 1,
            category = Category.Sights,
            name = R.string.kremlin_name,
            shortDescription = R.string.kremlin_short,
            address = R.string.kremlin_address,
            description = R.string.kremlin_description,
            icon = Icons.Filled.Fort,
        ),
        Place(
            id = 2,
            category = Category.Sights,
            name = R.string.kul_sharif_name,
            shortDescription = R.string.kul_sharif_short,
            address = R.string.kul_sharif_address,
            description = R.string.kul_sharif_description,
            icon = Icons.Filled.Mosque,
        ),
        Place(
            id = 3,
            category = Category.Sights,
            name = R.string.syuyumbike_name,
            shortDescription = R.string.syuyumbike_short,
            address = R.string.syuyumbike_address,
            description = R.string.syuyumbike_description,
            icon = Icons.Filled.LocationCity,
        ),
        Place(
            id = 4,
            category = Category.Sights,
            name = R.string.bauman_name,
            shortDescription = R.string.bauman_short,
            address = R.string.bauman_address,
            description = R.string.bauman_description,
            icon = Icons.AutoMirrored.Filled.DirectionsWalk,
        ),
        Place(
            id = 5,
            category = Category.Sights,
            name = R.string.farmers_palace_name,
            shortDescription = R.string.farmers_palace_short,
            address = R.string.farmers_palace_address,
            description = R.string.farmers_palace_description,
            icon = Icons.Filled.Villa,
        ),
        // Museums
        Place(
            id = 6,
            category = Category.Museums,
            name = R.string.national_museum_name,
            shortDescription = R.string.national_museum_short,
            address = R.string.national_museum_address,
            description = R.string.national_museum_description,
            icon = Icons.Filled.HistoryEdu,
        ),
        Place(
            id = 7,
            category = Category.Museums,
            name = R.string.hermitage_name,
            shortDescription = R.string.hermitage_short,
            address = R.string.hermitage_address,
            description = R.string.hermitage_description,
            icon = Icons.Filled.AccountBalance,
        ),
        Place(
            id = 8,
            category = Category.Museums,
            name = R.string.fine_arts_name,
            shortDescription = R.string.fine_arts_short,
            address = R.string.fine_arts_address,
            description = R.string.fine_arts_description,
            icon = Icons.Filled.Palette,
        ),
        Place(
            id = 9,
            category = Category.Museums,
            name = R.string.soc_byt_name,
            shortDescription = R.string.soc_byt_short,
            address = R.string.soc_byt_address,
            description = R.string.soc_byt_description,
            icon = Icons.Filled.Chair,
        ),
        Place(
            id = 10,
            category = Category.Museums,
            name = R.string.chak_chak_museum_name,
            shortDescription = R.string.chak_chak_museum_short,
            address = R.string.chak_chak_museum_address,
            description = R.string.chak_chak_museum_description,
            icon = Icons.Filled.EmojiFoodBeverage,
        ),
        // Parks and embankments
        Place(
            id = 11,
            category = Category.Parks,
            name = R.string.kremlin_embankment_name,
            shortDescription = R.string.kremlin_embankment_short,
            address = R.string.kremlin_embankment_address,
            description = R.string.kremlin_embankment_description,
            icon = Icons.Filled.NaturePeople,
        ),
        Place(
            id = 12,
            category = Category.Parks,
            name = R.string.kaban_name,
            shortDescription = R.string.kaban_short,
            address = R.string.kaban_address,
            description = R.string.kaban_description,
            icon = Icons.Filled.Waves,
        ),
        Place(
            id = 13,
            category = Category.Parks,
            name = R.string.black_lake_name,
            shortDescription = R.string.black_lake_short,
            address = R.string.black_lake_address,
            description = R.string.black_lake_description,
            icon = Icons.Filled.Park,
        ),
        Place(
            id = 14,
            category = Category.Parks,
            name = R.string.gorky_park_name,
            shortDescription = R.string.gorky_park_short,
            address = R.string.gorky_park_address,
            description = R.string.gorky_park_description,
            icon = Icons.Filled.Forest,
        ),
        // Tatar cuisine
        Place(
            id = 15,
            category = Category.Food,
            name = R.string.echpochmak_name,
            shortDescription = R.string.echpochmak_short,
            address = R.string.food_where,
            description = R.string.echpochmak_description,
            icon = Icons.Filled.BakeryDining,
        ),
        Place(
            id = 16,
            category = Category.Food,
            name = R.string.chak_chak_name,
            shortDescription = R.string.chak_chak_short,
            address = R.string.food_where,
            description = R.string.chak_chak_description,
            icon = Icons.Filled.Cookie,
        ),
        Place(
            id = 17,
            category = Category.Food,
            name = R.string.gubadia_name,
            shortDescription = R.string.gubadia_short,
            address = R.string.food_where,
            description = R.string.gubadia_description,
            icon = Icons.Filled.Cake,
        ),
        Place(
            id = 18,
            category = Category.Food,
            name = R.string.kystyby_name,
            shortDescription = R.string.kystyby_short,
            address = R.string.food_where,
            description = R.string.kystyby_description,
            icon = Icons.Filled.LunchDining,
        ),
        Place(
            id = 19,
            category = Category.Food,
            name = R.string.talkysh_name,
            shortDescription = R.string.talkysh_short,
            address = R.string.food_where,
            description = R.string.talkysh_description,
            icon = Icons.Filled.Icecream,
        ),
        // Theaters and circus
        Place(
            id = 20,
            category = Category.Theaters,
            name = R.string.opera_name,
            shortDescription = R.string.opera_short,
            address = R.string.opera_address,
            description = R.string.opera_description,
            icon = Icons.Filled.MusicNote,
        ),
        Place(
            id = 21,
            category = Category.Theaters,
            name = R.string.kamal_name,
            shortDescription = R.string.kamal_short,
            address = R.string.kamal_address,
            description = R.string.kamal_description,
            icon = Icons.Filled.TheaterComedy,
        ),
        Place(
            id = 22,
            category = Category.Theaters,
            name = R.string.kachalov_name,
            shortDescription = R.string.kachalov_short,
            address = R.string.kachalov_address,
            description = R.string.kachalov_description,
            icon = Icons.Filled.AutoStories,
        ),
        Place(
            id = 23,
            category = Category.Theaters,
            name = R.string.circus_name,
            shortDescription = R.string.circus_short,
            address = R.string.circus_address,
            description = R.string.circus_description,
            icon = Icons.Filled.Festival,
        ),
        Place(
            id = 24,
            category = Category.Theaters,
            name = R.string.ekiyat_name,
            shortDescription = R.string.ekiyat_short,
            address = R.string.ekiyat_address,
            description = R.string.ekiyat_description,
            icon = Icons.Filled.Castle,
        ),
    )

    fun getPlaces(category: Category): List<Place> =
        allPlaces.filter { it.category == category }
}
