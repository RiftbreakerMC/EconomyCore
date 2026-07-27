package net.tnemc.core.menu.page.shared;

/*
 * The New Economy
 * Copyright (C) 2022 - 2024 Daniel "creatorfromhell" Vidmar
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

import net.kyori.adventure.text.Component;
import net.tnemc.core.TNECore;
import net.tnemc.core.account.Account;
import net.tnemc.core.account.PlayerAccount;
import net.tnemc.core.config.GuiLayoutConfig;
import net.tnemc.core.menu.icons.shared.PreviousPageIcon;
import net.tnemc.item.AbstractItemStack;
import net.tnemc.item.providers.SkullProfile;
import net.tnemc.menu.core.builder.IconBuilder;
import net.tnemc.menu.core.callbacks.page.PageOpenCallback;
import net.tnemc.menu.core.icon.action.ActionType;
import net.tnemc.menu.core.icon.action.impl.DataAction;
import net.tnemc.menu.core.icon.action.impl.SwitchPageAction;
import net.tnemc.menu.core.viewer.MenuViewer;
import net.tnemc.plugincore.PluginCore;
import net.tnemc.plugincore.core.io.message.MessageData;
import net.tnemc.plugincore.core.io.message.MessageHandler;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * AccountSelectionPage
 *
 * @author creatorfromhell
 * @since 0.1.3.0
 */
public class AccountSelectionPage {

  protected final String returnMenu;
  protected final String menuName;
  protected final int menuPage;
  protected final int returnPage;
  protected final String accountDataID;
  protected final String accountPageID;
  protected final int menuRows;

  public AccountSelectionPage(final String accountDataID, final String returnMenu, final String menuName,
                              final int menuPage, final int returnPage, final String accountPageID, final int menuRows) {

    this.returnMenu = returnMenu;
    this.menuName = menuName;
    this.menuPage = menuPage;
    this.returnPage = returnPage;
    this.accountDataID = accountDataID;
    this.accountPageID = accountPageID;

    //we need a controller row and then at least one row for items.
    this.menuRows = (menuRows <= 1)? 2 : menuRows;
  }

  public void handle(final PageOpenCallback callback) {

    final Optional<MenuViewer> viewer = callback.getPlayer().viewer();
    if(viewer.isPresent()) {

      final UUID id = viewer.get().uuid();

      callback.getPage().addIcon(new PreviousPageIcon(id, GuiLayoutConfig.slot("SharedAccountSelection", "Back", 0, menuRows), this.menuName, 1, ActionType.ANY));

      final int page = (Integer)viewer.get().dataOrDefault(accountPageID, 1);
      final int items = (menuRows - 1) * 9;
      final int start = ((page - 1) * 9);

      final int maxPages = (TNECore.eco().account().getAccounts().size() / items) + (((TNECore.eco().account().getAccounts().size() % items) > 0)? 1 : 0);

      final int prev = (page <= 1)? maxPages : page - 1;
      final int next = (page >= maxPages)? 1 : page + 1;

      if(maxPages > 1) {

        callback.getPage().addIcon(new IconBuilder(PluginCore.server().stackBuilder().of("RED_WOOL", 1)
                                                           .customName(MessageHandler.grab(new MessageData("Messages.Menu.Shared.PreviousPageDisplay"), id))
                                                           .lore(Collections.singletonList(MessageHandler.grab(new MessageData("Messages.Menu.Shared.PreviousPage"), id))))
                                           .withActions(new DataAction(accountPageID, prev), new SwitchPageAction(menuName, menuPage))
                                           .withSlot(GuiLayoutConfig.slot("SharedAccountSelection", "PreviousPage", 0, menuRows))
                                           .build());

        callback.getPage().addIcon(new IconBuilder(PluginCore.server().stackBuilder().of("GREEN_WOOL", 1)
                                                           .customName(MessageHandler.grab(new MessageData("Messages.Menu.Shared.NextPageDisplay"), id))
                                                           .lore(Collections.singletonList(MessageHandler.grab(new MessageData("Messages.Menu.Shared.NextPage"), id))))
                                           .withActions(new DataAction(accountPageID, next), new SwitchPageAction(menuName, menuPage))
                                           .withSlot(GuiLayoutConfig.slot("SharedAccountSelection", "NextPage", 8, menuRows))
                                           .build());
      }

      callback.getPage().addIcon(new IconBuilder(PluginCore.server().stackBuilder().of("BARRIER", 1)
                                                         .customName(MessageHandler.grab(new MessageData("Messages.Menu.Shared.EscapeDisplay"), id))
                                                         .lore(Collections.singletonList(MessageHandler.grab(new MessageData("Messages.Menu.Shared.Escape"), id))))
                                         .withActions(new SwitchPageAction(returnMenu, returnPage))
                                         .withSlot(GuiLayoutConfig.slot("SharedAccountSelection", "Escape", 4, menuRows))
                                         .build());

      final List<Integer> slots = GuiLayoutConfig.slots("SharedAccountSelection", "Items", 9, 10, 11, 12, 13, 14, 15, 16, 17,
                                                        18, 19, 20, 21, 22, 23, 24, 25, 26,
                                                        27, 28, 29, 30, 31, 32, 33, 34, 35,
                                                        36, 37, 38, 39, 40, 41, 42, 43, 44,
                                                        45, 46, 47, 48, 49, 50, 51, 52, 53);
      int slot = 0;
      int i = 0;
      for(final Map.Entry<String, Account> entry : TNECore.eco().account().getAccounts().entrySet()) {

        if(i < start) {

          i++;

          continue;
        }
        if(i >= (start + items)) break;
        if(slot >= slots.size()) break;

        SkullProfile profile = null;
        try {

          if(entry.getValue() instanceof PlayerAccount) {
            profile = new SkullProfile();

            if(PluginCore.server().playedBefore(UUID.fromString(entry.getKey()))) {
              profile.uuid(UUID.fromString(entry.getKey()));
            }
          }

        } catch(final Exception ignore) { }

        AbstractItemStack<?> stack = PluginCore.server().stackBuilder().of("PLAYER_HEAD", 1)
                .customName(Component.text(entry.getValue().getName()))
                .lore(Collections.singletonList(MessageHandler.grab(new MessageData("Messages.Menu.MyEco.Account.Select"), id)));

        if(profile != null) {
          stack = stack.profile(profile);
        }


        callback.getPage().addIcon(new IconBuilder(stack)
                                           .withActions(new DataAction(accountDataID + "_ID", entry.getKey()),
                                                        new DataAction(accountDataID + "_NAME", entry.getValue().getName()),
                                                        new SwitchPageAction(returnMenu, returnPage))
                                           .withSlot(slots.get(slot))
                                           .build());

        i++;
        slot++;
      }
    }
  }
}
