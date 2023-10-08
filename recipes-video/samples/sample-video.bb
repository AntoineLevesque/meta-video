SUMMARY = "bitbake-layers recipe"
DESCRIPTION = "Recipe created by bitbake-layers"
LICENSE = "CLOSED"

DEPENDS += "pkgconfig-native gstreamer1.0 glib-2.0"

VIDEO_FILE = "${THISDIR}/files/qb_helmet_sample.mp4"
SRC_URI = "file://src"

FILES:${PN} += "${datadir}/movies"
FILES:${PN} += "${datadir}/movies/qb_helmet_sample.mp4"

# Where to keep downloaded source files (in tmp/work/...)
S = "${WORKDIR}/src"

# Pass arguments to linker
TARGET_CC_ARCH += "${LDFLAGS}"

# Cross-compile source code
do_compile(){
    ${CC} -o stream-sample-to-uvc stream-sample-to-uvc.c `pkg-config --cflags --libs gstreamer-1.0`
}


do_install(){
    # Create /usr/bin in rootfs and copy program to it
    install -d ${D}${bindir}
    install -m 0755 stream-sample-to-uvc ${D}${bindir}

    # Create /etc/share/movies and copy the .mp4 file to it
    install -d ${D}${datadir}/movies
    install -m 0755 ${VIDEO_FILE} ${D}${datadir}/movies
}

