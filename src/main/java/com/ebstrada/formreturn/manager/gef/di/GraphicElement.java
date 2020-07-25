package com.ebstrada.formreturn.manager.gef.di;

import java.awt.Rectangle;

/**
 * An interface to be implemented by connectable figs.
 */
public interface GraphicElement {

    public String getId();

    public Rectangle routingRect();

    public Object getOwner();
}
