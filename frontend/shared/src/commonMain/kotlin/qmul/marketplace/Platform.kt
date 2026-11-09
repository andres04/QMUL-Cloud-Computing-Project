package qmul.marketplace

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform