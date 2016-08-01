package org.flowgrid.swt.widget;


import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

public class ContextMenu {
    Menu swtMenu;
    ItemClickListener itemClickListener;
    ContextMenu.Item parentItem;

    public ContextMenu(Control control) {
        swtMenu = new Menu(control);
        // control.setMenu(swtMenu);  FIXME
    }

    ContextMenu(Item item) {
        swtMenu = new Menu(item.swtItem);
        parentItem = item;
    }

    public void dispose() {
        swtMenu.dispose();
    }

    public Item add(String label) {
        return new Item(this, label, false);
    }

    public Item addSubMenu(String label) {
        return new Item(this, label, true);
    }

    public void setOnMenuItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void show() {
        swtMenu.setVisible(true);
    }

    public static class Item implements SelectionListener {
        ContextMenu parentMenu;
        MenuItem swtItem;
        ContextMenu subMenu;

        Item(ContextMenu parent, String title, boolean sub) {
            this.parentMenu = parent;
            if (sub) {
                swtItem = new MenuItem(parent.swtMenu, SWT.CASCADE);
                subMenu = new ContextMenu(this);
            } else {
                swtItem = new MenuItem(parent.swtMenu, SWT.PUSH);
            }
            swtItem.setText(title);
            swtItem.addSelectionListener(this);
        }

        public ContextMenu getSubMenu() {
            return subMenu;
        }

        @Override
        public void widgetSelected(SelectionEvent e) {
            ContextMenu.Item current = this;
            do {
                ContextMenu parentMenu = current.parentMenu;
                if (parentMenu == null) {
                    return;
                }
                if (parentMenu.itemClickListener != null) {
                    parentMenu.itemClickListener.onContextMenuItemClick(current);
                    return;
                }
                current = parentMenu.parentItem;
            } while (current != null);
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent e) {
            widgetSelected(e);
        }

        public String getTitle() {
            return swtItem.getText();
        }
    }

    public interface ItemClickListener {
        boolean onContextMenuItemClick(Item item);
    }

}