#include <gst/gst.h>

static GMainLoop *loop;
static GstElement *pipeline, *source, *demuxer, *decoder, *conv, *sink;
static GstBus *bus;
static guint bus_watch_id;

static gboolean bus_call (GstBus *bus, GstMessage *msg, gpointer data)
{
    GMainLoop *loop = (GMainLoop *) data;

    switch (GST_MESSAGE_TYPE (msg)) {

    case GST_MESSAGE_EOS:
        /* restart playback if at end */
         if (!gst_element_seek(pipeline, 
                    1.0, GST_FORMAT_TIME, GST_SEEK_FLAG_FLUSH,
                    GST_SEEK_TYPE_SET,  0, // start
                    GST_SEEK_TYPE_NONE, GST_CLOCK_TIME_NONE)) {
            g_print("Seek failed!\n");
        }

        /* g_print ("End of stream\n"); */
        /* g_main_loop_quit (loop); */
        break;

    case GST_MESSAGE_ERROR: {
        gchar  *debug;
        GError *error;

        gst_message_parse_error (msg, &error, &debug);
        g_free (debug);

        g_printerr ("Error: %s\n", error->message);
        g_error_free (error);

        g_main_loop_quit (loop);
        break;
    }
    default:
        break;
    }

    return TRUE;
}

static void on_pad_added (GstElement *element, GstPad *pad, gpointer data)
{
    GstPad *sinkpad;
    GstElement *decoder = (GstElement *) data;

    /* We can now link this pad with the vorbis-decoder sink pad */
    g_print ("Dynamic pad created, linking demuxer/decoder\n");

    sinkpad = gst_element_get_static_pad (decoder, "sink");

    gst_pad_link (pad, sinkpad);

    gst_object_unref (sinkpad);
}


int main (int argc, char *argv[])
{
    /* Initialize GStreamer */
    gst_init (&argc, &argv);

    loop = g_main_loop_new (NULL, FALSE);

    /* Create the empty pipeline */
    pipeline = gst_pipeline_new ("mp4-playback");

    /* Create the elements */
    source = gst_element_factory_make ("filesrc", "file-source");
    demuxer = gst_element_factory_make ("qtdemux", "h264-demuxer"); 
    decoder = gst_element_factory_make ("avdec_h264", "h264-decoder");
    conv = gst_element_factory_make("videoconvert", "converter");
    sink = gst_element_factory_make ("v4l2sink", "sink");
    /* uvcsink */

    if (!pipeline || !source || !demuxer || !decoder || !conv || !sink) {
    g_printerr ("Not all elements could be created.\n");
    return -1;
    }

    /* Modify the source's properties */
    g_object_set (G_OBJECT(source), "location", "/usr/share/movies/qb_helmet_sample.mp4", NULL);
    /* Modify the sink's properties*/
    g_object_set(G_OBJECT(sink), "device", "/dev/video0", NULL);

    /* we add a message handler */
    bus = gst_pipeline_get_bus (GST_PIPELINE (pipeline));
    bus_watch_id = gst_bus_add_watch (bus, bus_call, loop);
    gst_object_unref (bus);


    /* Build the pipeline */
    gst_bin_add_many (GST_BIN (pipeline), source, demuxer, decoder, conv, sink, NULL);


    if(gst_element_link (source, demuxer) != TRUE)
    {
        g_printerr("Source and Demuxer could not be linked.\n");
        gst_object_unref(pipeline);
        return -1;
    }

    if (gst_element_link_many (decoder, conv, sink, NULL) != TRUE)
    {
        g_printerr("Elements could not be linked.\n");
        gst_object_unref(pipeline);
        return -1;
    }

    g_signal_connect (demuxer, "pad-added", G_CALLBACK (on_pad_added), decoder);


    /* Start playing */
    gst_element_set_state (pipeline, GST_STATE_PLAYING);

    /* Iterate */
    g_print ("Running...\n");
    g_main_loop_run (loop);

    /* Out of the main loop, clean up nicely */
    g_print ("Returned, stopping playback\n");
    gst_element_set_state (pipeline, GST_STATE_NULL);

    g_print ("Deleting pipeline\n");
    gst_object_unref (GST_OBJECT (pipeline));
    g_source_remove (bus_watch_id);
    g_main_loop_unref (loop);

    return 0;
}