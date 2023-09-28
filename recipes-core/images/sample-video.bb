SUMMARY = "bitbake-layers recipe"
DESCRIPTION = "Recipe created by bitbake-layers"
LICENSE = "MIT"

python do_display_banner() {
    bb.plain("***********************************************");
    bb.plain("*                                             *");
    bb.plain("*  Copying the sample video to the rootfs     *");
    bb.plain("*                                             *");
    bb.plain("***********************************************");
}

addtask display_banner before do_build

MY_FILES = "/home/antoine/code/sama5d27/meta-video/usr/share/movies/qb_helmet_sample.mp4"

FILES:${PN} += "${datadir}/movies"
FILES:${PN} += "${datadir}/movies/qb_helmet_sample.mp4"

do_install(){
    install -d ${D}${datadir}/movies
    install -m 0644 ${MY_FILES} ${D}${datadir}/movies
}

