package assembtec.com.snach;

import assembtec.com.snach_core_lib.ActionAppContentItem;
import assembtec.com.snach_core_lib.ListAppContentItem;
import assembtec.com.snach_core_lib.PopUpContentItem;
import assembtec.com.snach_core_lib.SnachNotification;

/**
 * Created by Giorgio on 15.06.15.
 */
public class BufferItem {
    private boolean hasLayout = false;
    private boolean hasContent = false;
    /**
     * Set hasReachedEOB to true for all items which do only have layout data.
     */
    private boolean hasReachedEOB = false;
    private int bufferIndex1 = 0;
    private int bufferIndex2 = 19;

    /**
     * LayoutBuffers are buffers which consist of only 1 package of 20 bytes
     * and don't have a EOB_Byte.
     */
    private byte [] BUFFER_LAYOUT;
    /**
     * ContentBuffers can be bigger than 20 bytes and thus may be sent in multiple
     * packages. The bufferIndexes and the hsReachedEOB take care that the right
     * sub array is sent and that the content buffer is sent completely.
     */
    private byte [] BUFFER_CONTENT;

    private ActionAppContentItem aaci;
    private ListAppContentItem laci;
    private SnachNotification snachNotification;
    private PopUpContentItem puci;
    private int MODE = 0;

    public boolean isHasLayout() {
        return hasLayout;
    }

    public void setHasLayout(boolean hasLayout) {
        this.hasLayout = hasLayout;
    }

    public boolean isHasContent() {
        return hasContent;
    }

    public void setHasContent(boolean hasContent) {
        this.hasContent = hasContent;
    }

    public boolean isHasReachedEOB() {
        return hasReachedEOB;
    }

    public void setHasReachedEOB(boolean hasReachedEOB) {
        this.hasReachedEOB = hasReachedEOB;
    }

    public int getBufferIndex1() {
        return bufferIndex1;
    }

    public void setBufferIndex1(int bufferIndex1) {
        this.bufferIndex1 = bufferIndex1;
    }

    public int getBufferIndex2() {
        return bufferIndex2;
    }

    public void setBufferIndex2(int bufferIndex2) {
        this.bufferIndex2 = bufferIndex2;
    }

    public byte[] getBUFFER_LAYOUT() {
        return BUFFER_LAYOUT;
    }

    public void setBUFFER_LAYOUT(byte[] BUFFER_LAYOUT) {
        this.BUFFER_LAYOUT = BUFFER_LAYOUT;
    }

    public byte[] getBUFFER_CONTENT() {
        return BUFFER_CONTENT;
    }

    public void setBUFFER_CONTENT(byte[] BUFFER_CONTENT) {
        this.BUFFER_CONTENT = BUFFER_CONTENT;
    }

    public ActionAppContentItem getActionAppContentItem() {
        return aaci;
    }

    public void setActionAppContentItem(ActionAppContentItem aaci) {
        this.aaci = aaci;
    }

    public ListAppContentItem getListAppContentItem() {
        return laci;
    }

    public void setListAppContentItem(ListAppContentItem laci) {
        this.laci = laci;
    }

    public SnachNotification getSnachNotification() {
        return snachNotification;
    }

    public void setSnachNotification(SnachNotification snachNotification) {
        this.snachNotification = snachNotification;
    }

    public PopUpContentItem getPuci() {
        return puci;
    }

    public void setPuci(PopUpContentItem puci) {
        this.puci = puci;
    }

    public int getMODE() {
        return MODE;
    }

    public void setMODE(int MODE) {
        this.MODE = MODE;
    }
}
