package org.herac.tuxguitar.gui.system.keybindings.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.system.keybindings.KeyBinding;
import org.herac.tuxguitar.gui.system.keybindings.KeyBindingReserveds;
import org.herac.tuxguitar.gui.util.ConfirmDialog;
import org.herac.tuxguitar.gui.util.DialogUtils;
import org.herac.tuxguitar.gui.util.MessageDialog;

public class KeyBindingSelector {
	
	protected Shell dialog;
	protected KeyBindingEditor editor;
	protected KeyBinding keyBinding;
	
	public KeyBindingSelector(KeyBindingEditor editor,KeyBinding keyBinding){
		this.editor = editor;
		this.keyBinding = keyBinding;
	}
	
	public KeyBinding select(Shell parent){			
		this.dialog = DialogUtils.newDialog(parent,SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM);
		this.dialog.setLayout(new GridLayout());
		
		final Composite composite = new Composite(this.dialog,SWT.NONE);
		composite.setLayout(new GridLayout());
		composite.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		composite.setFocus();
		composite.addKeyListener(new KeyAdapter() {			
			public void keyReleased(KeyEvent e) {					
				KeyBinding kb = new KeyBinding(e.keyCode,e.stateMask);
				if(kb.equals(KeyBindingSelector.this.keyBinding) || isValid(kb)){
					if(KeyBindingSelector.this.keyBinding == null){
						KeyBindingSelector.this.keyBinding = new KeyBinding();
					}						
					KeyBindingSelector.this.keyBinding.setKey(kb.getKey());
					KeyBindingSelector.this.keyBinding.setMask(kb.getMask());
					KeyBindingSelector.this.dialog.dispose();
				}
				else{
					composite.setFocus();
				}
			}					
		});
		final Font font = new Font(this.dialog.getDisplay(),"Sans", 15, SWT.BOLD);
		Label label = new Label(composite,SWT.LEFT);
		label.setFont(font);
		label.setText(TuxGuitar.getProperty("key-bindings-editor-push-a-key"));		
		label.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent arg0) {
				font.dispose();
			}
		});
        //------------------BUTTONS--------------------------            
        Composite buttons = new Composite(this.dialog, SWT.NONE);
        buttons.setLayout(new GridLayout(2,false));
        buttons.setLayoutData(new GridData(SWT.RIGHT,SWT.FILL,true,true));
      
        final Button buttonClean = new Button(buttons, SWT.PUSH);
        buttonClean.setText(TuxGuitar.getProperty("clean"));
        buttonClean.setLayoutData(getButtonData());
        buttonClean.addMouseListener(new MouseAdapter() {
			public void mouseUp(MouseEvent e) {
				composite.setFocus();
			}
		});
        buttonClean.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent arg0) {
            	KeyBindingSelector.this.keyBinding = null;
            	KeyBindingSelector.this.dialog.dispose();
            }
        });

        Button buttonCancel = new Button(buttons, SWT.PUSH);
        buttonCancel.setText(TuxGuitar.getProperty("cancel"));
        buttonCancel.setLayoutData(getButtonData());
        buttonCancel.addMouseListener(new MouseAdapter() {
			public void mouseUp(MouseEvent e) {
				composite.setFocus();
			}
		});
        buttonCancel.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent arg0) {
            	KeyBindingSelector.this.dialog.dispose();
            }
        });		
		
		DialogUtils.openDialog(this.dialog, DialogUtils.OPEN_STYLE_CENTER | DialogUtils.OPEN_STYLE_PACK | DialogUtils.OPEN_STYLE_WAIT);
        
        return this.keyBinding;
	}				

	private GridData getButtonData(){
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.minimumWidth = 80;
		data.minimumHeight = 25;
		return data;
	}	
	
	protected boolean isValid(KeyBinding kb){
		if(KeyBindingReserveds.isReserved(kb)){
			MessageDialog.infoMessage(this.dialog,TuxGuitar.getProperty("key-bindings-editor-reserved-title"),TuxGuitar.getProperty("key-bindings-editor-reserved-message"));
			return false;
		}
		if(this.editor.exists(kb)){
			ConfirmDialog confirm = new ConfirmDialog(TuxGuitar.getProperty("key-bindings-editor-override"));
			if(confirm.confirm(ConfirmDialog.BUTTON_YES | ConfirmDialog.BUTTON_NO, ConfirmDialog.BUTTON_NO) == ConfirmDialog.STATUS_NO){
				return false;
			}
		}
		return true;
	}
	
}