/*
 * Copyright (C) 2011, 2012 University of Szeged
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY UNIVERSITY OF SZEGED ``AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL UNIVERSITY OF SZEGED OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY
 * OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.arm.nevada.client.view;

import com.arm.nevada.client.view.shared.NumberFormat;
import com.arm.nevada.client.view.shared.RegisterViewSettings;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.Widget;

public class TypeSelectorMenu extends Composite implements HasValue<RegisterViewSettings> {

	private static TypeSelectorMenuUiBinder uiBinder = GWT.create(TypeSelectorMenuUiBinder.class);

	interface TypeSelectorMenuUiBinder extends UiBinder<Widget, TypeSelectorMenu> {
	}

	@UiField
	MenuItem sd8, ud8, sh8, uh8, sd16, ud16, sh16, uh16, sd32, ud32, sh32, uh32, sd64, ud64, sh64, uh64, f16, f32;
	@UiField
	MenuItem rootItem, doubleWideMeuItem;

	// private EventBus eventBus;
	private RegisterViewSettings elementViewSettings;
	private boolean doubleWordWide;

	public TypeSelectorMenu() {
		this(true);
	}

	public TypeSelectorMenu(boolean doubleWordWide) {
		initWidget(uiBinder.createAndBindUi(this));
		elementViewSettings = new RegisterViewSettings();
		this.setDoubleWordWide(doubleWordWide);
		subscribeCommands();
		sd8.getCommand().execute();
		setDoubleWordWide(doubleWordWide);
	}

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<RegisterViewSettings> handler) {
		return this.addHandler(handler, ValueChangeEvent.getType());
	}

	@Override
	public RegisterViewSettings getValue() {
		return new RegisterViewSettings(elementViewSettings);
	}

	@Override
	public void setValue(RegisterViewSettings value) {
		this.elementViewSettings = value;
		updateText();

	}

	private void updateText() {
		String out = elementViewSettings.getText();
		rootItem.setText(out);
	}

	@Override
	public void setValue(RegisterViewSettings value, boolean fireEvents) {
		setValue(value);
		if (fireEvents)
			ValueChangeEvent.fire(this, new RegisterViewSettings(this.elementViewSettings));
	}

	private void subscribeCommands() {

		sd8.setCommand(new Command() {
			@Override
			public void execute() {
				setValue(new RegisterViewSettings(false, 8, true, NumberFormat.decimal), true);
			}
		});

		ud8.setCommand(new Command() {
			@Override
			public void execute() {
				setValue(new RegisterViewSettings(false, 8, false, NumberFormat.decimal), true);
			}
		});

		sh8.setCommand(new Command() {
			@Override
			public void execute() {
				setValue(new RegisterViewSettings(false, 8, true, NumberFormat.hexadecimal), true);
			}
		});

		uh8.setCommand(new Command() {
			@Override
			public void execute() {
				setValue(new RegisterViewSettings(false, 8, false, NumberFormat.hexadecimal), true);
			}
		});

		sd16.setCommand(new Command() {
			@Override
			public void execute() {
				setValue(new RegisterViewSettings(false, 16, true, NumberFormat.decimal), true);
			}
		});

		ud16.setCommand(new Command() {
			@Override
			public void execute() {
				setValue(new RegisterViewSettings(false, 16, false, NumberFormat.decimal), true);
			}
		});

		sh16.setCommand(new Command() {
			@Override
			public void execute() {
				setValue(new RegisterViewSettings(false, 16, true, NumberFormat.hexadecimal), true);
			}
		});

		uh16.setCommand(new Command() {
			@Override
			public void execute() {
				setValue(new RegisterViewSettings(false, 16, false, NumberFormat.hexadecimal), true);
			}
		});

		sd32.setCommand(new Command() {
			@Override
			public void execute() {
				setValue(new RegisterViewSettings(false, 32, true, NumberFormat.decimal), true);
			}
		});

		ud32.setCommand(new Command() {
			@Override
			public void execute() {
				setValue(new RegisterViewSettings(false, 32, false, NumberFormat.decimal), true);
			}
		});

		sh32.setCommand(new Command() {
			@Override
			public void execute() {
				setValue(new RegisterViewSettings(false, 32, true, NumberFormat.hexadecimal), true);
			}
		});

		uh32.setCommand(new Command() {
			@Override
			public void execute() {
				setValue(new RegisterViewSettings(false, 32, false, NumberFormat.hexadecimal), true);
			}
		});

		sd64.setCommand(new Command() {
			@Override
			public void execute() {
				setValue(new RegisterViewSettings(false, 64, true, NumberFormat.decimal), true);
			}
		});

		ud64.setCommand(new Command() {
			@Override
			public void execute() {
				setValue(new RegisterViewSettings(false, 64, false, NumberFormat.decimal), true);
			}
		});

		sh64.setCommand(new Command() {
			@Override
			public void execute() {
				setValue(new RegisterViewSettings(false, 64, true, NumberFormat.hexadecimal), true);
			}
		});

		uh64.setCommand(new Command() {
			@Override
			public void execute() {
				setValue(new RegisterViewSettings(false, 64, false, NumberFormat.hexadecimal), true);
			}
		});

		f16.setCommand(new Command() {
			@Override
			public void execute() {
				setValue(new RegisterViewSettings(true, 16, true, NumberFormat.decimal), true);
			}
		});

		f32.setCommand(new Command() {
			@Override
			public void execute() {
				setValue(new RegisterViewSettings(true, 32, true, NumberFormat.decimal), true);
			}
		});
	}

	public boolean isDoubleWordWide() {
		return doubleWordWide;
	}

	public void setDoubleWordWide(boolean doubleWordWide) {
		this.doubleWordWide = doubleWordWide;
		doubleWideMeuItem.setVisible(doubleWordWide);
	}

}
