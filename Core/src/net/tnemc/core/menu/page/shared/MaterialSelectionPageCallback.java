package net.tnemc.core.menu.page.shared;
/*
 * The New Menu Library
 * Copyright (C) 2022 - 2023 Daniel "creatorfromhell" Vidmar
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import net.tnemc.core.config.GuiLayoutConfig;
import net.tnemc.core.menu.handlers.StringSelectionHandler;
import net.tnemc.menu.core.builder.IconBuilder;
import net.tnemc.menu.core.callbacks.page.PageOpenCallback;
import net.tnemc.menu.core.icon.action.impl.DataAction;
import net.tnemc.menu.core.icon.action.impl.RunnableAction;
import net.tnemc.menu.core.icon.action.impl.SwitchPageAction;
import net.tnemc.menu.core.manager.MenuManager;
import net.tnemc.menu.core.viewer.MenuViewer;
import net.tnemc.plugincore.PluginCore;
import net.tnemc.plugincore.core.io.message.MessageData;
import net.tnemc.plugincore.core.io.message.MessageHandler;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * MaterialSelectionMenu
 *
 * @author creatorfromhell
 * @since 1.5.0.0
 */
public class MaterialSelectionPageCallback {

  protected final String returnMenu;
  protected final String menuName;
  protected final int menuPage;
  protected final int returnPage;
  protected final String materialDataID;
  protected final String materialPageID;
  protected final int menuRows;

  protected final Consumer<StringSelectionHandler> selectionListener;

  public MaterialSelectionPageCallback(final String materialDataID, final String returnMenu, final String menuName,
                                       final int menuPage, final int returnPage, final String materialPageID,
                                       final int menuRows, final Consumer<StringSelectionHandler> selectionListener) {

    this.returnMenu = returnMenu;
    this.menuName = menuName;
    this.menuPage = menuPage;
    this.returnPage = returnPage;
    this.materialDataID = materialDataID;
    this.materialPageID = materialPageID;

    //we need a controller row and then at least one row for items.
    this.menuRows = (menuRows <= 1)? 2 : menuRows;
    this.selectionListener = selectionListener;
  }

  public void handle(final PageOpenCallback callback) {

    final Optional<MenuViewer> viewer = callback.getPlayer().viewer();
    if(viewer.isPresent()) {

      final UUID id = viewer.get().uuid();

      final int page = (Integer)viewer.get().dataOrDefault(materialPageID, 1);
      final int items = (menuRows - 1) * 9;
      final int start = ((page - 1) * items);

      final int maxPages = (MenuManager.instance().getHelper().materials().size() / items) + (((MenuManager.instance().getHelper().materials().size() % items) > 0)? 1 : 0);

      final int prev = (page <= 1)? maxPages : page - 1;
      final int next = (page >= maxPages)? 1 : page + 1;

      if(maxPages > 1) {

        callback.getPage().addIcon(new IconBuilder(PluginCore.server().stackBuilder().of("RED_WOOL", 1)
                                                           .customName(MessageHandler.grab(new MessageData("Messages.Menu.Shared.PreviousPageDisplay"), id))
                                                           .lore(Collections.singletonList(MessageHandler.grab(new MessageData("Messages.Menu.Shared.PreviousPage"), id))))
                                           .withActions(new DataAction(materialPageID, prev), new SwitchPageAction(menuName, menuPage))
                                           .withSlot(GuiLayoutConfig.slot("SharedMaterialSelection", "PreviousPage", 0, menuRows))
                                           .build());

        callback.getPage().addIcon(new IconBuilder(PluginCore.server().stackBuilder().of("GREEN_WOOL", 1)
                                                           .customName(MessageHandler.grab(new MessageData("Messages.Menu.Shared.NextPageDisplay"), id))
                                                           .lore(Collections.singletonList(MessageHandler.grab(new MessageData("Messages.Menu.Shared.NextPage"), id))))
                                           .withActions(new DataAction(materialPageID, next), new SwitchPageAction(menuName, menuPage))
                                           .withSlot(GuiLayoutConfig.slot("SharedMaterialSelection", "NextPage", 8, menuRows))
                                           .build());
      }

      callback.getPage().addIcon(new IconBuilder(PluginCore.server().stackBuilder().of("BARRIER", 1)
                                                         .customName(MessageHandler.grab(new MessageData("Messages.Menu.Shared.EscapeDisplay"), id))
                                                         .lore(Collections.singletonList(MessageHandler.grab(new MessageData("Messages.Menu.Shared.Escape"), id))))
                                         .withActions(new SwitchPageAction(returnMenu, returnPage))
                                         .withSlot(GuiLayoutConfig.slot("SharedMaterialSelection", "Escape", 4, menuRows))
                                         .build());

      final List<Integer> slots = GuiLayoutConfig.slots("SharedMaterialSelection", "Items", 9, 10, 11, 12, 13, 14, 15, 16, 17,
                                                        18, 19, 20, 21, 22, 23, 24, 25, 26,
                                                        27, 28, 29, 30, 31, 32, 33, 34, 35,
                                                        36, 37, 38, 39, 40, 41, 42, 43, 44,
                                                        45, 46, 47, 48, 49, 50, 51, 52, 53);
      int slot = 0;
      for(int i = start; i < start + items; i++) {
        if(MenuManager.instance().getHelper().materials().size() <= i) {
          break;
        }
        if(slot >= slots.size()) {
          break;
        }

        final String material = MenuManager.instance().getHelper().materials().get(i);

        callback.getPage().addIcon(new IconBuilder(PluginCore.server().stackBuilder().of(material, 1)
                                                           .lore(Collections.singletonList(MessageHandler.grab(new MessageData("Messages.Menu.MyEco.Material.Select"), id))))
                                           .withActions(new DataAction(materialDataID, material),
                                                        new RunnableAction((click)->{

                                                          if(selectionListener != null) {

                                                            selectionListener.accept(new StringSelectionHandler(click, material));
                                                          }
                                                        }), new SwitchPageAction(returnMenu, returnPage))
                                           .withSlot(slots.get(slot))
                                           .build());
        slot++;
      }
    }
  }
}
