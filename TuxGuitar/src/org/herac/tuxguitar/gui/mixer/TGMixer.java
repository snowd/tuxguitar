/*
 * Created on 20-mar-2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.gui.mixer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.helper.SyncThread;
import org.herac.tuxguitar.gui.system.icons.IconLoader;
import org.herac.tuxguitar.gui.system.language.LanguageLoader;
import org.herac.tuxguitar.gui.undo.undoables.track.UndoableTrackChannel;
import org.herac.tuxguitar.gui.util.DialogUtils;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGChannel;
import org.herac.tuxguitar.song.models.TGTrack;

/**
 * @author julian
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class TGMixer implements IconLoader,LanguageLoader{	
	
	public static final int MUTE = 0x01;
	public static final int SOLO = 0x02;
	public static final int VOLUME = 0x04;
	public static final int BALANCE = 0x08;
	public static final int CHANNEL = 0x10;
	public static final int CHANGE_ALL = (MUTE | SOLO | VOLUME | BALANCE | CHANNEL);
	
	protected Shell dialog;
	protected TGSongManager manager;
	protected Scale volumeScale;
	protected Text volumeText;
	private Label volumeLabel;
	private List trackMixers;
	protected String tipVolume;
	
	protected UndoableTrackChannel undoableVolume;
	
	public TGMixer() {
		this.manager = TuxGuitar.instance().getSongManager();
		this.trackMixers = new ArrayList();
		TuxGuitar.instance().getIconManager().addLoader(this);
		TuxGuitar.instance().getLanguageManager().addLoader(this);
	}

	public void show() {
		this.dialog = DialogUtils.newDialog(TuxGuitar.instance().getShell(), SWT.DIALOG_TRIM);
		this.loadData();

		TuxGuitar.instance().updateCache(true);
		
		DialogUtils.openDialog(this.dialog, DialogUtils.OPEN_STYLE_CENTER | DialogUtils.OPEN_STYLE_WAIT);
		
		TuxGuitar.instance().updateCache(true);
	}
		
	protected void loadData(){		
		this.trackMixers.clear();
		Iterator it = TuxGuitar.instance().getSongManager().getSong().getTracks();
		while (it.hasNext()) {
			TGTrack track = (TGTrack) it.next();
			TGMixerTrack trackMixer = new TGMixerTrack(this,track);
			trackMixer.init(this.dialog);
			this.trackMixers.add(trackMixer);
		}
		Composite composite = new Composite(this.dialog, SWT.NONE);
		composite.setLayout(new GridLayout());
		composite.setLayoutData(new GridData(SWT.CENTER,SWT.FILL,true,true));

		this.volumeLabel = new Label(composite, SWT.NONE);
		
		this.volumeScale = new Scale(composite, SWT.VERTICAL);
		this.volumeScale.setMaximum(10);
		this.volumeScale.setMinimum(0);
		this.volumeScale.setIncrement(1);
		this.volumeScale.setPageIncrement(1);		
		this.volumeScale.setLayoutData(new GridData(SWT.CENTER,SWT.FILL,true,true));
		
		this.volumeText = new Text(composite, SWT.BORDER | SWT.SINGLE | SWT.CENTER);		
		this.volumeText.setEditable(false);
		this.volumeText.setLayoutData(getVolumeTextData());

		this.volumeScale.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				int volume = (short)(TGMixer.this.volumeScale.getMaximum() - TGMixer.this.volumeScale.getSelection());
				if(volume != TGMixer.this.manager.getSong().getVolume()){
					TGMixer.this.manager.getSong().setVolume(volume);
					TGMixer.this.volumeScale.setToolTipText(TGMixer.this.tipVolume + ": " + TGMixer.this.manager.getSong().getVolume());
					TGMixer.this.volumeText.setText(Integer.toString(TGMixer.this.volumeScale.getMaximum() - TGMixer.this.volumeScale.getSelection()));
					if (TuxGuitar.instance().getPlayer().isRunning()) {
						TuxGuitar.instance().getPlayer().updateControllers();
					}				
				}
			}
		});
		this.volumeScale.addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent arg0) {
				TGMixer.this.undoableVolume = UndoableTrackChannel.startUndo();
			}			
			public void mouseUp(MouseEvent arg0) {
				if(TGMixer.this.undoableVolume != null){
					TuxGuitar.instance().getUndoableManager().addEdit(TGMixer.this.undoableVolume.endUndo());
					TuxGuitar.instance().getFileHistory().setUnsavedFile();
					TuxGuitar.instance().updateCache(true);
					TGMixer.this.undoableVolume = null;
				}
			}
		});	
		

		this.loadVolume();
		this.loadIcons();
		this.loadProperties();		
		this.dialog.setLayout(new GridLayout(this.dialog.getChildren().length, false));
		this.dialog.pack();		
	}
	
	private void loadVolume(){
		this.volumeScale.setSelection(this.volumeScale.getMaximum() - this.manager.getSong().getVolume());
		this.volumeText.setText(Integer.toString(this.volumeScale.getMaximum() - this.volumeScale.getSelection()));
	}
	
	private GridData getVolumeTextData(){
		GridData data = new GridData(SWT.CENTER,SWT.NONE,true,false);		
		data.minimumWidth = 40;		
		return data;
	}
	
	protected void clear(){
        Control[] controls = this.dialog.getChildren();
        for(int i = 0;i < controls.length;i++){
            controls[i].dispose();
        }
	}

	public boolean isDisposed() {
		return (this.dialog == null || this.dialog.isDisposed());
	}
	
	public synchronized void fireChanges(TGChannel channel,int type){
		Iterator it = this.trackMixers.iterator();
		while(it.hasNext()){
			TGMixerTrack mixer = (TGMixerTrack)it.next();
			if(mixer.getTrack().getChannel().getChannel() == channel.getChannel()){
				mixer.getTrack().getChannel().setEffectChannel(channel.getEffectChannel());
				mixer.getTrack().getChannel().setVolume(channel.getVolume());
				mixer.getTrack().getChannel().setBalance(channel.getBalance());
				mixer.getTrack().getChannel().setChorus(channel.getChorus());
				mixer.getTrack().getChannel().setReverb(channel.getReverb());
				mixer.getTrack().getChannel().setPhaser(channel.getPhaser());
				mixer.getTrack().getChannel().setTremolo(channel.getTremolo());
			}
			mixer.fireChanges(type);
		}
		if (TuxGuitar.instance().getPlayer().isRunning()) {
			TuxGuitar.instance().getPlayer().updateControllers();			
		}
	}

	public synchronized void loadProperties(){
		if(!isDisposed()){
			Iterator it = this.trackMixers.iterator();
			while(it.hasNext()){
				TGMixerTrack mixer = (TGMixerTrack)it.next();
				mixer.loadProperties();
			}
			this.volumeLabel.setText(TuxGuitar.getProperty("mixer.volume") + ":");
			this.tipVolume = TuxGuitar.getProperty("mixer.volume");
			this.volumeScale.setToolTipText(this.tipVolume + ": " + this.manager.getSong().getVolume());			
			this.dialog.setText(TuxGuitar.getProperty("mixer"));
			this.dialog.pack();
		}
	}
	
	public synchronized void loadIcons(){
		if(!isDisposed()){
			this.dialog.setImage(TuxGuitar.instance().getIconManager().getAppIcon());
		}
	}	
	
	public synchronized void updateItems(){
		if(!isDisposed()){
			Iterator it = this.trackMixers.iterator();
			while(it.hasNext()){
				TGMixerTrack mixer = (TGMixerTrack)it.next();
				mixer.updateItems();
			}
		}
	}

	public synchronized void updateValues(){
		if(!isDisposed()){
			this.loadVolume();
			
			Iterator it = this.trackMixers.iterator();
			while(it.hasNext()){
				TGMixerTrack mixer = (TGMixerTrack)it.next();
				mixer.fireChanges(CHANGE_ALL);
			}
		}
	}	
	
	public synchronized void update(){
		if(!isDisposed()){
			new SyncThread(new Runnable() {
				public void run() {
					if(!isDisposed()){
						TGMixer.this.clear();
						TGMixer.this.loadData();
						TGMixer.this.dialog.layout();
					}
				}
			}).start();
		}
	}
	
	public synchronized void dispose() {
		if(!isDisposed()){
			this.dialog.dispose();
		}
	}
}