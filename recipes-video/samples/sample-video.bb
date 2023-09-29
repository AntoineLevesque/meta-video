SUMMARY = "bitbake-layers recipe"
DESCRIPTION = "Recipe created by bitbake-layers"
LICENSE = "CLOSED"

VIDEO_FILE = "${THISDIR}/files/qb_helmet_sample.mp4"

FILES:${PN} += "${datadir}/movies"
FILES:${PN} += "${datadir}/movies/qb_helmet_sample.mp4"

do_install(){
    install -d ${D}${datadir}/movies
    install -m 0644 ${VIDEO_FILE} ${D}${datadir}/movies
}

