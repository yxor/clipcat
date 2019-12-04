package com.clipcat;


/**
 * Class to wrap clipboard data.
 */
public class ClipboardObject{
    byte[] data;
    ObjectType objectType;

    /**
     * @param data          binary data.
     * @param objectType    type of the binary data.
     */
    public ClipboardObject(byte[] data, ObjectType objectType)
    {
        this.data = data;
        this.objectType = objectType;
    }

}