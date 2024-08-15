package com.balsam.upgradetable.util;

import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;


public class ScreenUtil {
    public static boolean checkRange(Widget widget, double mouseX, double mouseY){
        return widget.x<= mouseX && mouseX >= widget.x+widget.getWidth() &&
                widget.y<= mouseY && mouseY >= widget.y+widget.getHeight();
    }
    public static boolean checkClick(Button button, double mouseX, double mouseY){
        return checkRange(button, mouseX, mouseY) && button.active;
    }
}
