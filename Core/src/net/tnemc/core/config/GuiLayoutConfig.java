package net.tnemc.core.config;

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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * GuiLayoutConfig
 *
 * @author creatorfromhell
 * @since 0.1.4.1
 */
public class GuiLayoutConfig {

  private static final String ROOT = "Core.GUI.Layout.";
  private static final int MIN_ROWS = 1;
  private static final int MAX_ROWS = 6;

  public static int rows(final String menu, final int fallback) {

    return bounded(MainConfig.yaml().getInt(ROOT + menu + ".Rows", fallback), MIN_ROWS, MAX_ROWS);
  }

  public static int slot(final String menu, final String key, final int fallback) {

    return slot(menu, key, fallback, MAX_ROWS);
  }

  public static int slot(final String menu, final String key, final int fallback, final int rows) {

    return bounded(MainConfig.yaml().getInt(ROOT + menu + ".Slots." + key, fallback), 0, (rows * 9) - 1);
  }

  public static List<Integer> slots(final String menu, final String key, final int... fallback) {

    final List<Integer> configured = MainConfig.yaml().getStringList(ROOT + menu + ".Slots." + key)
            .stream()
            .map(GuiLayoutConfig::parseSlot)
            .filter(slot->slot >= 0 && slot < (MAX_ROWS * 9))
            .collect(Collectors.toList());

    if(!configured.isEmpty()) {
      return configured;
    }
    return Arrays.stream(fallback).boxed().collect(Collectors.toList());
  }

  private static int parseSlot(final String slot) {

    try {
      return Integer.parseInt(slot);
    } catch(final NumberFormatException ignore) { }
    return -1;
  }

  private static int bounded(final int value, final int min, final int max) {

    return Math.max(min, Math.min(max, value));
  }
}
