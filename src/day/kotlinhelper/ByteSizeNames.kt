package day.kotlinhelper

/**
 * contains names of different amount of downloaded data
 */
enum class ByteSizeNames (val name : String) {
    K("kilobytes"),
    M("megabytes"),
    G("gigabytes"),
    T("terabytes"),
    P("petabytes"),
    E("exabytes"),
    Z("zetabytes"),
    Y("yottabytes"),
    N9("1024^9 bytes"),
    N10("1024^10 bytes"),
    N11("1024^11 bytes"),
    N12("1024^12 bytes"),
    N13("1024^13 bytes")
}