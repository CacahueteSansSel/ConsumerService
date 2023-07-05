package dev.cacahuete.consume.ui;

import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;

public class ConsumerItemStackHandler extends ItemStackHandler {

    public IContentsChangedHandler handler;

    public ConsumerItemStackHandler(@Nullable IContentsChangedHandler handler, int size) {
        super(size);
        this.handler = handler;
    }

    @Override
    protected void onContentsChanged(int slot) {
        super.onContentsChanged(slot);
        if (handler == null) return;
        handler.onContentsChanged(slot);
    }

    public static interface IContentsChangedHandler {
        void onContentsChanged(int slot);
    }
}
