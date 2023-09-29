SUMMARY = "Adds usb-uvc capabilities, h264 encoding"
IMAGE_INSTALL = "packagegroup-core-boot ${CORE_IMAGE_EXTRA_INSTALL}"
IMAGE_LINGUAS = " "
LICENSE = "CLOSED"


python do_display_banner() {
    bb.plain("***********************************************");
    bb.plain("*                                             *");
    bb.plain("*  Poor man's eye tracking incoming!          *");
    bb.plain("*                                             *");
    bb.plain("***********************************************");
}

addtask display_banner before do_build

inherit core-image

IMAGE_INSTALL:append = "\
    gstreamer1.0 \
    opencv \
    sample-video \
"

# Set rootfs to 1 GB by default
IMAGE_OVERHEAD_FACTOR ?= "1.0"
IMAGE_ROOTFS_SIZE ?= "1003520"
#IMAGE_ROOTFS_EXTRA_SPACE:append = "${@bb.utils.contains("DISTRO_FEATURES", "systemd", " + 4096", "", d)}"

inherit extrausers
# Change root password (note the capital -P)
EXTRA_USERS_PARAMS = " \
    usermod -p '\$6\$11223344\$N9q7Gt5eJJb54sBuR/.7ih8w/OeBBQ8D38FmefDKuTMVM5RbnR07FQayQQWSntbXw/DPpuS781UWLHDTY1lhr1' root; \
"