package day.kotlinhelper

/**
 * represents size of file
 */
enum class FileSize(val bytes: Long) {
    Byte(1L),
    K(1024L),
    M(1024L*1024L),
    G(1024L*1024L*1024L);
    companion object FileSizeConverter {
        fun tryParse(value : String) : FileSize? {
            try {
                return FileSize.valueOf(value.toUpperCase())
            } catch(ex: IllegalArgumentException) {
                return null;
            }
        }
    }
}
