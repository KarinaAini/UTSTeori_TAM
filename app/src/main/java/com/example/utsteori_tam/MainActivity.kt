package com.example.utsteori_tam

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.ui.graphics.Brush
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation.NavController
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import kotlinx.coroutines.launch
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.compositionLocalOf
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import com.example.utsteori_tam.ui.theme.UTSTeori_TAMTheme
import android.content.Context
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Person
import tiket.KonserSource

val LocalNav = compositionLocalOf<NavController> { error("No NavController") }
data class Ticket(
    val ticketId: String,
    val concertId: String,
    val qty: Int,
    val seats: List<String>
)

val userTickets = mutableStateListOf<Ticket>()

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val navController = rememberNavController()

            UTSTeori_TAMTheme {
                CompositionLocalProvider(LocalNav provides navController) {

                    NavHost(
                        navController = navController,
                        startDestination = "login"
                    ) {

                        composable("login") { LoginScreen() }
                        composable("register") { RegisterScreen() }
                        composable("home") { DashboardScreen() }
                        composable("profile") { ProfileScreen() }

                        composable("detail/{concertId}") {
                            val id = it.arguments?.getString("concertId") ?: "1"
                            ConcertDetailScreen(id)
                        }

                        composable("ticket/{concertId}") {
                            val id = it.arguments?.getString("concertId") ?: "1"
                            SelectTicketScreen(id)
                        }

                        composable("qty/{ticketId}/{concertId}") { backStackEntry ->
                            val ticketId = backStackEntry.arguments?.getString("ticketId") ?: ""
                            val concertId = backStackEntry.arguments?.getString("concertId") ?: ""
                            QuantityScreen(ticketId, concertId)
                        }

                        composable("checkout/{ticketId}/{concertId}/{qty}") { backStackEntry ->
                            val ticketId = backStackEntry.arguments?.getString("ticketId") ?: ""
                            val concertId = backStackEntry.arguments?.getString("concertId") ?: ""
                            val qty = backStackEntry.arguments?.getString("qty")?.toInt() ?: 1
                            CheckoutScreen(ticketId, concertId, qty)
                        }

                        composable("myticket") { MyTicketScreen() }

                        composable("ticketdetail/{ticketId}") {
                            val id = it.arguments?.getString("ticketId") ?: "A12"
                            TicketDetailScreen(id)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GradientButton(text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(
                brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                    listOf(Color(0xFF8E2DE2), Color(0xFFFF4DA6))
                ),
                shape = RoundedCornerShape(25.dp)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = Color.White, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun LoginScreen() {
    val nav = LocalNav.current
    val context = LocalContext.current

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                "TicketNow",
                color = Color(0xFF8E2DE2),
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(20.dp))

            Image(
                painter = painterResource(R.drawable.konsero),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.height(20.dp))

            InputField(username, { username = it }, "Username")
            Spacer(Modifier.height(10.dp))
            InputField(password, { password = it }, "Password")

            Spacer(Modifier.height(20.dp))

            GradientButton("Login") {

                val pref = context.getSharedPreferences("USER", Context.MODE_PRIVATE)
                val users = pref.getString("users", "")?.split(";") ?: emptyList()

                val success = users.any { user ->
                    val parts = user.split("|")
                    parts.size == 3 &&
                            parts[0] == username &&
                            parts[2] == password
                }

                if (success) {
                    pref.edit().putString("currentUser", username).apply()

                    nav.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                } else {
                    scope.launch {
                        snackbar.showSnackbar("Username / Password salah")
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            Text(
                "Belum punya akun? Daftar disini",
                color = Color(0xFFFF4DA6),
                modifier = Modifier.clickable {
                    nav.navigate("register")
                }
            )

            Spacer(Modifier.height(10.dp))

            SnackbarHost(hostState = snackbar)
        }
    }
}

@Composable
fun RegisterScreen() {
    val nav = LocalNav.current
    val context = LocalContext.current

    var nama by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                "TicketNow",
                color = Color(0xFF8E2DE2),
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(20.dp))

            Image(
                painter = painterResource(R.drawable.konsero),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.height(20.dp))

            InputField(nama, { nama = it }, "Username")
            Spacer(Modifier.height(10.dp))
            InputField(email, { email = it }, "Email")
            Spacer(Modifier.height(10.dp))
            InputField(password, { password = it }, "Password")

            Spacer(Modifier.height(20.dp))

            GradientButton("Sign Up") {

                val pref = context.getSharedPreferences("USER", Context.MODE_PRIVATE)
                val oldData = pref.getString("users", "") ?: ""

                val newUser = "$nama|$email|$password"

                val updatedData = if (oldData.isEmpty()) {
                    newUser
                } else {
                    "$oldData;$newUser"
                }

                pref.edit().putString("users", updatedData).apply()

                scope.launch {
                    snackbar.showSnackbar("Registrasi berhasil")
                }

                nav.navigate("login") {
                    popUpTo("register") { inclusive = true }
                }
            }

            Spacer(Modifier.height(10.dp))

            Text(
                "Sudah punya akun? Login!",
                color = Color(0xFFFF4DA6),
                modifier = Modifier.clickable {
                    nav.popBackStack()
                }
            )

            Spacer(Modifier.height(10.dp))

            SnackbarHost(hostState = snackbar)
        }
    }
}

@Composable
fun InputField(value: String, onChange: (String) -> Unit, hint: String) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        placeholder = { Text(hint) },
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun DashboardScreen() {
    val nav = LocalNav.current
    val data = KonserSource.listKonser

    var search by remember { mutableStateOf("") }

    val filtered = data.filter {
        it.nama.contains(search, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(Color(0xFFF7F7F7))
            .padding(16.dp)
    ) {

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "TicketNow",
                color = Color(0xFF8E2DE2),
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = search,
            onValueChange = { search = it },
            placeholder = { Text("Search concerts...") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(20.dp)
        )

        Spacer(Modifier.height(16.dp))

        Text("Explore", fontWeight = FontWeight.Bold)

        Spacer(Modifier.height(8.dp))

        if (data.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .clickable {
                        val konser = data[0]
                        nav.navigate("detail/${konser.nama}")
                    }
            ) {
                Image(
                    painter = painterResource(data[0].imageRes),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                ) {
                    Text(
                        data[0].nama,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(6.dp))

                    GradientButton("Book Ticket") {
                        val konser = data[0]
                        nav.navigate("detail/${konser.nama}")
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Popular Concerts", fontWeight = FontWeight.Bold)
            Text("See All", color = Color.Gray)
        }

        Spacer(Modifier.height(8.dp))

        LazyRow {
            items(filtered) { konser ->
                Card(
                    modifier = Modifier
                        .width(180.dp)
                        .padding(end = 12.dp)
                        .clickable { nav.navigate("detail/${konser.nama}") },
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column {

                        Image(
                            painter = painterResource(konser.imageRes),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .clip(
                                    RoundedCornerShape(
                                        topStart = 16.dp,
                                        topEnd = 16.dp
                                    )
                                ),
                            contentScale = ContentScale.Crop
                        )

                        Column(Modifier.padding(8.dp)) {
                            Text(
                                konser.nama,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )

                            Text(
                                konser.lokasi,
                                color = Color.Gray,
                                fontSize = MaterialTheme.typography.bodySmall.fontSize
                            )
                        }
                    }
                }
            }
        }
        Spacer(Modifier.weight(1f))
        BottomNav(current = "home")
    }
}

@Composable
fun BottomNav(current: String) {
    val nav = LocalNav.current

    val active = Color(0xFF8E2DE2)
    val inactive = Color.Gray

    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 12.dp)
            .background(Color.White, RoundedCornerShape(20.dp))
            .padding(12.dp)
            .navigationBarsPadding(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.clickable { nav.navigate("home") }
        ) {
            Icon(Icons.Default.Home, null, tint = if (current == "home") active else inactive)
            Text("Beranda", color = if (current == "home") active else inactive)
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.clickable { nav.navigate("myticket") }
        ) {
            Icon(Icons.Default.ConfirmationNumber, null, tint = if (current == "myticket") active else inactive)
            Text("My Tickets", color = if (current == "myticket") active else inactive)
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.clickable { nav.navigate("profile") }
        ) {
            Icon(Icons.Default.Person, null, tint = if (current == "profile") active else inactive)
            Text("Profile", color = if (current == "profile") active else inactive)
        }
    }
}

@Composable
fun ConcertDetailScreen(concertId: String) {
    val nav = LocalNav.current

    val konser = KonserSource.listKonser.find { it.nama == concertId }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F7))
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = { nav.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
            }
        }

        Spacer(Modifier.height(12.dp))
        Image(
            painter = painterResource(konser?.imageRes ?: android.R.drawable.ic_menu_gallery),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(20.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(Modifier.height(16.dp))
        Text(
            konser?.nama ?: "",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(Modifier.height(12.dp))
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

            Text("📍 ${konser?.lokasi ?: ""}", color = Color.Gray)

            Text("📅 ${konser?.tanggal ?: ""}", color = Color.Gray)

            Text("⏰ 18:00 WIB", color = Color.Gray)
        }

        Spacer(Modifier.height(16.dp))
        Text(
            "Detail Event",
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(6.dp))

        Text(
            konser?.deskripsi ?: "",
            color = Color.Gray
        )

        Spacer(Modifier.height(30.dp))
        GradientButton("Select Ticket") {
            nav.navigate("ticket/${konser?.nama}")
        }

        Spacer(Modifier.height(10.dp))
    }
}

@Composable
fun SelectTicketScreen(concertId: String) {
    val nav = LocalNav.current

    var selected by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F7))
            .statusBarsPadding()
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { nav.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
            }
            Text("Select Ticket", fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.height(16.dp))
        Image(
            painter = painterResource(R.drawable.stage),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(170.dp)
                .clip(RoundedCornerShape(20.dp)),
            contentScale = ContentScale.Fit
        )

        Spacer(Modifier.height(20.dp))
        TicketItem(
            title = "CAT 1",
            desc = "Near Stage",
            price = "Rp3.000.000",
            color = Color(0xFF8E2DE2),
            selected = selected == "CAT1"
        ) {
            selected = "CAT1"
        }

        TicketItem(
            title = "CAT 2",
            desc = "Middle Area",
            price = "Rp2.000.000",
            color = Color(0xFFFF4DA6),
            selected = selected == "CAT2"
        ) {
            selected = "CAT2"
        }

        TicketItem(
            title = "CAT 3",
            desc = "Upper Area",
            price = "Rp1.200.000",
            color = Color(0xFFFFA726),
            selected = selected == "CAT3"
        ) {
            selected = "CAT3"
        }

        Spacer(Modifier.weight(1f))
        GradientButton("Continue") {
            if (selected.isNotEmpty()) {
                nav.navigate("qty/$selected/$concertId")
            }
        }

        Spacer(
            Modifier
                .navigationBarsPadding()
                .height(8.dp)
        )
    }
}

@Composable
fun TicketItem(
    title: String,
    desc: String,
    price: String,
    color: Color,
    selected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) Color(0xFFEDE7F6) else Color(0xFFE0E0E0)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Row(verticalAlignment = Alignment.CenterVertically) {

                Box(
                    modifier = Modifier
                        .width(60.dp)
                        .height(40.dp)
                        .background(color, RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        title,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(Modifier.width(14.dp))

                Column {
                    Text(desc, fontWeight = FontWeight.Bold)
                    Text("Area", color = Color.Gray)
                }
            }

            Text(price, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun QuantityScreen(ticketId: String, concertId: String) {
    val nav = LocalNav.current
    var qty by remember { mutableStateOf(1) }

    val konser = KonserSource.listKonser.find { it.nama == concertId }

    val (categoryName, price, color) = when (ticketId) {
        "CAT1" -> Triple("Near Stage", 3000000, Color(0xFF8E2DE2))
        "CAT2" -> Triple("Middle Area", 2000000, Color(0xFFFF4DA6))
        "CAT3" -> Triple("Upper Area", 1200000, Color(0xFFFFA726))
        else -> Triple("Regular", 1000000, Color.Gray)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F7))
            .statusBarsPadding()
            .padding(16.dp)
    ) {

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { nav.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
            }
            Text("Choose Quantity", fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.height(16.dp))

        Card(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Image(
                    painter = painterResource(
                        konser?.imageRes ?: android.R.drawable.ic_menu_gallery
                    ),
                    contentDescription = null,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(Modifier.width(12.dp))

                Column {
                    Text(konser?.nama ?: "-", fontWeight = FontWeight.Bold)
                    Text(konser?.lokasi ?: "-", color = Color.Gray)
                    Text("${konser?.tanggal ?: "-"} ", color = Color.Gray)
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Card(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .size(50.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Row(verticalAlignment = Alignment.CenterVertically) {

                    Box(
                        modifier = Modifier
                            .width(80.dp)
                            .height(50.dp)
                            .background(color, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            ticketId,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(Modifier.width(12.dp))

                    Column {
                        Text(categoryName, fontWeight = FontWeight.Bold)
                        Text("Area", color = Color.Gray)
                    }
                }

                Text("Rp$price", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(Modifier.height(20.dp))

        Text("Jumlah Tiket", fontWeight = FontWeight.Bold)

        Spacer(Modifier.height(12.dp))

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.LightGray, CircleShape)
                    .clickable { if (qty > 1) qty-- },
                contentAlignment = Alignment.Center
            ) {
                Text("-", fontWeight = FontWeight.Bold)
            }

            Text(
                qty.toString(),
                modifier = Modifier.padding(horizontal = 24.dp),
                fontWeight = FontWeight.Bold
            )

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(color, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "+",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { if (qty < 6) qty++ }
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        Text(
            "Maksimal 6 tiket per transaksi",
            color = Color.Gray,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(Modifier.weight(1f))
        Text(
            "Total: Rp${qty * price}",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        GradientButton("Continue") {
            nav.navigate("checkout/$ticketId/$concertId/$qty")
        }

        Spacer(
            Modifier
                .navigationBarsPadding()
                .height(8.dp)
        )
    }
}

fun generateSeats(): List<String> {
    val seats = mutableListOf<String>()

    val cat1Rows = listOf('A', 'B')
    val cat1PerRow = 100 / cat1Rows.size
    for (row in cat1Rows) {
        for (i in 1..cat1PerRow) seats.add("$row$i")
    }

    val cat2Rows = listOf('C', 'D', 'E')
    val cat2PerRow = 250 / cat2Rows.size
    for (row in cat2Rows) {
        for (i in 1..cat2PerRow) seats.add("$row$i")
    }

    val cat3Rows = listOf('F','G','H','I','J')
    val cat3PerRow = 500 / cat3Rows.size
    for (row in cat3Rows) {
        for (i in 1..cat3PerRow) seats.add("$row$i")
    }

    return seats.shuffled()
}


@Composable
fun CheckoutScreen(ticketId: String, concertId: String, qty: Int) {
    val nav = LocalNav.current

    val konser = KonserSource.listKonser.find { it.nama == concertId }

    val (categoryName, price) = when (ticketId) {
        "CAT1" -> "CAT 1 (Near Stage)" to 3000000
        "CAT2" -> "CAT 2 (Middle Area)" to 2000000
        else -> "CAT 3 (Upper Area)" to 1200000
    }

    val subtotal = price * qty
    val admin = 25000
    val total = subtotal + admin

    Column(
        Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F7))
            .statusBarsPadding()
            .padding(16.dp)
    ) {

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { nav.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
            }
            Text("Checkout", fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.height(16.dp))

        Card(shape = RoundedCornerShape(20.dp)) {
            Column(Modifier.padding(16.dp)) {

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(
                            konser?.imageRes ?: android.R.drawable.ic_menu_gallery
                        ),
                        contentDescription = null,
                        modifier = Modifier.size(60.dp),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(Modifier.width(12.dp))

                    Column {
                        Text(konser?.nama ?: "-", fontWeight = FontWeight.Bold)
                        Text(konser?.lokasi ?: "", color = Color.Gray)
                        Text("${konser?.tanggal}", color = Color.Gray)
                    }
                }

                Spacer(Modifier.height(16.dp))

                RowItem("Kategori", categoryName)
                RowItem("Jumlah Tiket", "$qty")
                RowItem("Subtotal", "Rp$subtotal")
                RowItem("Service Fee", "Rp$admin")

                Spacer(Modifier.height(12.dp))

                Text("Total: Rp$total", fontWeight = FontWeight.Bold)

                Spacer(Modifier.height(20.dp))

                GradientButton("Pay Now") {

                    val seats = generateSeats()
                        .filter {
                            when (ticketId) {
                                "CAT1" -> it.startsWith("A") || it.startsWith("B")
                                "CAT2" -> it.startsWith("C") || it.startsWith("D") || it.startsWith("E")
                                else -> it.startsWith("F") || it.startsWith("G") || it.startsWith("H") || it.startsWith("I") || it.startsWith("J")
                            }
                        }
                        .take(qty)

                    userTickets.add(
                        Ticket(ticketId, concertId, qty, seats)
                    )

                    nav.navigate("myticket")
                }
            }
        }
    }
}


@Composable
fun MyTicketScreen() {
    val nav = LocalNav.current

    Column(
        Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F7))
            .statusBarsPadding()
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { nav.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
            }

            Text(
                "My Tickets",
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(userTickets) { ticket ->

                val konser = KonserSource.listKonser.find { it.nama == ticket.concertId }

                val category = when (ticket.ticketId) {
                    "CAT1" -> "Near Stage"
                    "CAT2" -> "Middle Area"
                    else -> "Upper Area"
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(20.dp)
                ) {

                    Column(Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {

                            Image(
                                painter = painterResource(
                                    konser?.imageRes ?: android.R.drawable.ic_menu_gallery
                                ),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(RoundedCornerShape(12.dp)),
                                contentScale = ContentScale.Crop
                            )

                            Spacer(Modifier.width(12.dp))

                            Column {
                                Text(konser?.nama ?: "-", fontWeight = FontWeight.Bold)
                                Text(konser?.lokasi ?: "", color = Color.Gray)
                                Text("${konser?.tanggal}", color = Color.Gray)
                            }
                        }

                        Spacer(Modifier.height(12.dp))

                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Column {
                                Text(ticket.ticketId, fontWeight = FontWeight.Bold)
                                Text(category, color = Color.Gray)

                                Spacer(Modifier.height(8.dp))

                                Text(
                                    "Seat\n${ticket.seats.joinToString(", ")}",
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Image(
                                painter = painterResource(R.drawable.barcode),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(90.dp)
                                    .clip(RoundedCornerShape(12.dp)),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Spacer(Modifier.height(12.dp))

                        GradientButton("View Details") {
                            val seatsString = ticket.seats.joinToString(",")
                            nav.navigate("ticketdetail/$seatsString")
                        }
                    }
                }
            }
        }
        BottomNav(current = "myticket")
    }
}

@Composable
fun ProfileScreen() {
    val nav = LocalNav.current
    val context = LocalContext.current

    val pref = context.getSharedPreferences("USER", Context.MODE_PRIVATE)
    val data = pref.getString("users", "") ?: ""
    val currentUser = pref.getString("currentUser", "") ?: ""

    val users = data.split(";")

    val userData = users.find {
        it.split("|").getOrNull(0) == currentUser
    }?.split("|")

    val nama = userData?.getOrNull(0) ?: "Guest"

    val email = "${nama.lowercase().replace(" ", ".")}@gmail.com"

    Column(
        Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F7))
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .background(
                    brush = Brush.verticalGradient(
                        listOf(Color(0xFF8E2DE2), Color(0xFFFF4DA6))
                    ),
                    shape = RoundedCornerShape(
                        bottomStart = 40.dp,
                        bottomEnd = 40.dp
                    )
                )
        ) {

            IconButton(
                onClick = { nav.popBackStack() },
                modifier = Modifier.align(Alignment.TopStart).padding(top = 45.dp)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    tint = Color.White
                )
            }

            Text(
                "Profile",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.align(Alignment.TopCenter).padding(top = 50.dp)
            )

            Image(
                painter = painterResource(R.drawable.profile),
                contentDescription = null,
                modifier = Modifier
                    .size(130.dp)
                    .align(Alignment.BottomCenter)
                    .offset(y = 40.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(Modifier.height(60.dp))

        Column(Modifier.padding(20.dp)) {

            Text("Nama", color = Color(0xFF8E2DE2))

            Text(
                nama,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(Modifier.height(16.dp))

            Divider(color = Color.LightGray)

            Spacer(Modifier.height(16.dp))

            Text("Email", color = Color(0xFFFF4DA6))

            Text(
                email,
                fontWeight = FontWeight.Medium
            )

            Spacer(Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Row(verticalAlignment = Alignment.CenterVertically) {

                        Box(
                            modifier = Modifier.size(50.dp).clip(CircleShape).background(Color(0xFFFF4DA6)),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(R.drawable.pprofile),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize().clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Spacer(Modifier.width(12.dp))

                        Column {
                            Text("Ubah Foto Profil", fontWeight = FontWeight.Bold)
                            Text(
                                "Pilih foto baru untuk akun kamu",
                                color = Color.Gray
                            )
                        }
                    }

                    Text(">", color = Color.Gray)
                }
            }
        }

        Spacer(Modifier.weight(1f))

        BottomNav(current = "profile")
    }
}

@Composable
fun TicketDetailScreen(seats: String) {
    val nav = LocalNav.current
    val seatList = remember(seats) {
        if (seats.isNotBlank()) {
            seats.split(",").filter { it.isNotBlank() }
        } else {
            userTickets.lastOrNull()?.seats ?: emptyList()
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F7))
            .statusBarsPadding()
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { nav.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
            }
            Text("My Tickets", fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.height(16.dp))
        if (seatList.isEmpty()) {
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text("Tidak ada tiket", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {

                items(seatList) { seat ->

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFEAEAF0)
                        )
                    ) {

                        Column(
                            Modifier
                                .padding(20.dp)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            Image(
                                painter = painterResource(R.drawable.barcode),
                                contentDescription = null,
                                modifier = Modifier.size(200.dp),
                                contentScale = ContentScale.Fit
                            )

                            Spacer(Modifier.height(12.dp))

                            Text(
                                "seat : $seat",
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                "Tunjukkan QR saat masuk venue",
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
                .navigationBarsPadding()
        ) {
            GradientButton("Back Home") {
                nav.navigate("home")
            }
        }
    }
}

@Composable
fun RowItem(title: String, value: String) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(title, color = Color.Gray)
        Text(value)
    }
}