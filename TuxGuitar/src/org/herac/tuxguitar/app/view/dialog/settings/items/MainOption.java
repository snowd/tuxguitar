package org.herac.tuxguitar.app.view.dialog.settings.items;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.system.config.TGConfigKeys;
import org.herac.tuxguitar.app.util.WindowTitleUtil;
import org.herac.tuxguitar.app.view.dialog.settings.TGSettingsEditor;
import org.herac.tuxguitar.util.TGSynchronizer;

public class MainOption  extends Option{
	protected boolean initialized;
	protected Button showSplash;
	protected Button autoSizeTable;
	protected Text windowTitle;
	
	public MainOption(TGSettingsEditor configEditor,ToolBar toolBar,final Composite parent){
		super(configEditor,toolBar,parent,TuxGuitar.getProperty("settings.config.main"));
		this.initialized = false;
	}
	
	public void createOption(){
		getToolItem().setText(TuxGuitar.getProperty("settings.config.main"));
		getToolItem().setImage(TuxGuitar.getInstance().getIconManager().getOptionMain());
		getToolItem().addSelectionListener(this);
		
		showLabel(getComposite(),SWT.TOP | SWT.LEFT | SWT.WRAP,SWT.BOLD,0,TuxGuitar.getProperty("settings.config.main.window-title"));
		
		Composite windowTitleComposite = new Composite(getComposite(),SWT.NONE);
		windowTitleComposite.setLayout(new GridLayout());
		windowTitleComposite.setLayoutData(getTabbedData());
		
		this.windowTitle = new Text(windowTitleComposite,SWT.BORDER);
		this.windowTitle.setLayoutData(new GridData(SWT.FILL, SWT.FILL , true, true));
		this.windowTitle.setTextLimit(80);
		
		Composite infoHeader = new Composite(windowTitleComposite,SWT.NONE);
		infoHeader.setLayout(new GridLayout(2,false));
		infoHeader.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		
		showImageLabel(infoHeader,SWT.NONE,infoHeader.getDisplay().getSystemImage(SWT.ICON_INFORMATION));
		showLabel(infoHeader,SWT.TOP | SWT.LEFT | SWT.WRAP,SWT.NORMAL,0,TuxGuitar.getProperty("settings.config.main.window-title.help"));
		
		Composite infoBody = new Composite(windowTitleComposite,SWT.NONE);
		infoBody.setLayout(new GridLayout(2,false));
		infoBody.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		
		showLabel(infoBody, SWT.NONE,SWT.BOLD,-1,WindowTitleUtil.getVar(WindowTitleUtil.VAR_APP_NAME));
		showLabel(infoBody,SWT.TOP | SWT.WRAP,SWT.NORMAL,0,TuxGuitar.getProperty("settings.config.main.window-title.var.description." + WindowTitleUtil.VAR_APP_NAME ));
		
		showLabel(infoBody, SWT.NONE,SWT.BOLD,-1,WindowTitleUtil.getVar(WindowTitleUtil.VAR_APP_VERSION));
		showLabel(infoBody,SWT.TOP | SWT.WRAP,SWT.NORMAL,0,TuxGuitar.getProperty("settings.config.main.window-title.var.description." + WindowTitleUtil.VAR_APP_VERSION ));
		
		showLabel(infoBody, SWT.NONE,SWT.BOLD,-1,WindowTitleUtil.getVar(WindowTitleUtil.VAR_FILE_NAME));
		showLabel(infoBody,SWT.TOP | SWT.WRAP,SWT.NORMAL,0,TuxGuitar.getProperty("settings.config.main.window-title.var.description." + WindowTitleUtil.VAR_FILE_NAME ));
		
		showLabel(infoBody, SWT.NONE,SWT.BOLD,-1,WindowTitleUtil.getVar(WindowTitleUtil.VAR_FILE_PATH));
		showLabel(infoBody,SWT.TOP | SWT.WRAP,SWT.NORMAL,0,TuxGuitar.getProperty("settings.config.main.window-title.var.description." + WindowTitleUtil.VAR_FILE_PATH ));
		
		showLabel(infoBody, SWT.NONE,SWT.BOLD,-1,WindowTitleUtil.getVar(WindowTitleUtil.VAR_SONG_NAME));
		showLabel(infoBody,SWT.TOP | SWT.WRAP,SWT.NORMAL,0,TuxGuitar.getProperty("settings.config.main.window-title.var.description." + WindowTitleUtil.VAR_SONG_NAME ));
		
		showLabel(infoBody, SWT.NONE,SWT.BOLD,-1,WindowTitleUtil.getVar(WindowTitleUtil.VAR_SONG_ALBUM));
		showLabel(infoBody,SWT.TOP | SWT.WRAP,SWT.NORMAL,0,TuxGuitar.getProperty("settings.config.main.window-title.var.description." + WindowTitleUtil.VAR_SONG_ALBUM ));
		
		showLabel(infoBody, SWT.NONE,SWT.BOLD,-1,WindowTitleUtil.getVar(WindowTitleUtil.VAR_SONG_ARTIST));
		showLabel(infoBody,SWT.TOP | SWT.WRAP,SWT.NORMAL,0,TuxGuitar.getProperty("settings.config.main.window-title.var.description." + WindowTitleUtil.VAR_SONG_ARTIST ));
		
		showLabel(infoBody, SWT.NONE,SWT.BOLD,-1,WindowTitleUtil.getVar(WindowTitleUtil.VAR_SONG_AUTHOR));
		showLabel(infoBody,SWT.TOP | SWT.WRAP,SWT.NORMAL,0,TuxGuitar.getProperty("settings.config.main.window-title.var.description." + WindowTitleUtil.VAR_SONG_AUTHOR ));
		
		showLabel(getComposite(),SWT.BOTTOM | SWT.LEFT | SWT.WRAP,SWT.BOLD,0,TuxGuitar.getProperty("settings.config.main.options"));
		
		Composite options = new Composite(getComposite(),SWT.NONE);
		options.setLayout(new GridLayout());
		options.setLayoutData(getTabbedData());
		
		this.autoSizeTable = new Button(options,SWT.CHECK);
		this.autoSizeTable.setText(TuxGuitar.getProperty("settings.config.main.table.auto-size.enabled"));
		
		this.showSplash = new Button(options,SWT.CHECK);
		this.showSplash.setText(TuxGuitar.getProperty("settings.config.main.splash-enabled"));
		
		this.loadConfig();
	}
	
	protected void loadConfig(){
		new Thread(new Runnable() {
			public void run() {
				final String windowTitle = getConfig().getStringValue(TGConfigKeys.WINDOW_TITLE);
				final boolean showSplash = getConfig().getBooleanValue(TGConfigKeys.SHOW_SPLASH);
				final boolean autoSizeTable = getConfig().getBooleanValue(TGConfigKeys.TABLE_AUTO_SIZE);
				TGSynchronizer.getInstance(getViewContext().getContext()).executeLater(new Runnable() {
					public void run() {
						if(!isDisposed()){
							MainOption.this.windowTitle.setText(windowTitle);
							MainOption.this.showSplash.setSelection(showSplash);
							MainOption.this.autoSizeTable.setSelection(autoSizeTable);
							MainOption.this.initialized = true;
							MainOption.this.pack();
						}
					}
				});
			}
		}).start();
	}
	
	public void updateConfig(){
		if(this.initialized){
			getConfig().setValue(TGConfigKeys.WINDOW_TITLE,this.windowTitle.getText());
			getConfig().setValue(TGConfigKeys.SHOW_SPLASH,this.showSplash.getSelection());
			getConfig().setValue(TGConfigKeys.TABLE_AUTO_SIZE,this.autoSizeTable.getSelection());
		}
	}
	
	public void updateDefaults(){
		if(this.initialized){
			getConfig().setValue(TGConfigKeys.WINDOW_TITLE, getDefaults().getValue(TGConfigKeys.WINDOW_TITLE));
			getConfig().setValue(TGConfigKeys.SHOW_SPLASH, getDefaults().getValue(TGConfigKeys.SHOW_SPLASH));
			getConfig().setValue(TGConfigKeys.TABLE_AUTO_SIZE, getDefaults().getValue(TGConfigKeys.TABLE_AUTO_SIZE));
		}
	}
}
