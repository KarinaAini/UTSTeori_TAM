package tiket

import com.example.utsteori_tam.R

object KonserSource {

    val listKonser = listOf(

        Konser(
            nama = "NCT WISH - LOG IN JAKARTA",
            deskripsi = "NCT WISH menghadirkan konser 'LOG IN' di Jakarta dengan konsep fresh dan colorful.",
            lokasi = "Tennis Indoor Senayan, Jakarta",
            tanggal = "31 Mei 2025 • 15:00 WIB",
            harga = 2500000,
            imageRes = R.drawable.nct3
        ),

        Konser(
            nama = "SMTOWN LIVE 2025",
            deskripsi = "Konser spektakuler artis SM Entertainment di Gocheok Sky Dome.",
            lokasi = "Gocheok Sky Dome, Seoul Korea",
            tanggal = "11 - 12 Januari 2025",
            harga = 3000000,
            imageRes = R.drawable.nct2
        ),

        Konser(
            nama = "NCT DREAM - THE DREAM SHOW 2",
            deskripsi = "Tur dunia NCT DREAM dengan konsep megah 'The Dream Show 2'.",
            lokasi = "Seoul Olympic Stadium, Korea",
            tanggal = "2025",
            harga = 2800000,
            imageRes = R.drawable.nct1
        )
    )
}