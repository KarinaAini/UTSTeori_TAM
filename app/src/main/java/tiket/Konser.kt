package tiket

import androidx.annotation.DrawableRes

data class Konser(
    val nama: String,
    val deskripsi: String,
    val lokasi: String,
    val tanggal: String,
    val harga: Int,
    @DrawableRes val imageRes: Int
)