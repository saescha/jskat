package org.jskat.gui.action.main;

import org.jskat.control.JSkatEventBus;
import org.jskat.control.command.skatseries.ReplayGameCommand;
import org.jskat.data.JSkatApplicationData;
import org.jskat.gui.action.AbstractJSkatAction;
import org.jskat.gui.img.JSkatGraphicRepository.Icon;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Implements the action for replaying a game
 */
public class ReplayGameAction extends AbstractJSkatAction {


    /**
     * @see AbstractJSkatAction#AbstractJSkatAction()
     */
    public ReplayGameAction() {

        putValue(NAME, STRINGS.getString("replay_game"));
        putValue(SHORT_DESCRIPTION,
                STRINGS.getString("replay_game_tooltip"));

        setIcon(Icon.FIRST);
    }

    /**
     * @see AbstractAction#actionPerformed(ActionEvent)
     */
    @Override
    public void actionPerformed(final ActionEvent e) {
        JSkatEventBus.TABLE_EVENT_BUSSES.get(
                JSkatApplicationData.INSTANCE.getActiveTable()).post(
                new ReplayGameCommand());
    }
}
