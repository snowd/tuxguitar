package org.herac.tuxguitar.android.view.dialog.measure;

import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.android.R;
import org.herac.tuxguitar.android.view.dialog.TGDialog;
import org.herac.tuxguitar.android.view.util.SelectableItem;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.measure.TGCleanMeasureListAction;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGTrack;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class TGMeasureCleanDialog extends TGDialog {

	public TGMeasureCleanDialog() {
		super();
	}
	
	@SuppressLint("InflateParams")
	public Dialog onCreateDialog() {
		final TGTrack track = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK);
		final TGMeasure measure = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE);
		final View view = getActivity().getLayoutInflater().inflate(R.layout.view_measure_clean_dialog, null);
		
		this.fillRanges(view, track, measure);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.measure_clean_dlg_title);
		builder.setView(view);
		builder.setPositiveButton(R.string.global_button_ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				processAction(track, findSelectedMeasure1(view), findSelectedMeasure2(view));
				dialog.dismiss();
			}
		});
		builder.setNegativeButton(R.string.global_button_cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();
			}
		});
		
		return builder.create();
	}
	
	public SelectableItem[] createRangeValues(int minimum, int maximum) {
		List<SelectableItem> selectableItems = new ArrayList<SelectableItem>();
		for (int i = minimum; i <= maximum; i++) {
			selectableItems.add(new SelectableItem(Integer.valueOf(i), Integer.toString(i)));
		}
		SelectableItem[] builtItems = new SelectableItem[selectableItems.size()];
		selectableItems.toArray(builtItems);
		return builtItems;
	}
	
	public void fillSpinner(Spinner spinner, int minimum, int maximum) {
		spinner.setAdapter(new ArrayAdapter<SelectableItem>(getActivity(), android.R.layout.simple_spinner_item, createRangeValues(minimum, maximum)));
	}
	
	public void fillRanges(View view, TGTrack track, TGMeasure measure) {
		final int minimum = 1;
		final int maximum = track.countMeasures();
		
		final Spinner spinner1 = (Spinner) view.findViewById(R.id.measure_clean_dlg_from_value);
		final Spinner spinner2 = (Spinner) view.findViewById(R.id.measure_clean_dlg_to_value);
		
		this.fillSpinner(spinner1, minimum, maximum);
		this.fillSpinner(spinner2, minimum, maximum);
		
		this.updateSpinnerSelection(spinner1, measure.getNumber());
		this.updateSpinnerSelection(spinner2, measure.getNumber());
		
		spinner1.setOnItemSelectedListener(new OnItemSelectedListener() {
		    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
		    	validateSpinner1Selection(spinner1, spinner2, minimum, maximum);
		    }
		    public void onNothingSelected(AdapterView<?> parent) {
		    	validateSpinner1Selection(spinner1, spinner2, minimum, maximum);
		    }
		});
		
		spinner2.setOnItemSelectedListener(new OnItemSelectedListener() {
		    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
		    	validateSpinner2Selection(spinner1, spinner2, minimum, maximum);
		    }
		    public void onNothingSelected(AdapterView<?> parent) {
		    	validateSpinner2Selection(spinner1, spinner2, minimum, maximum);
		    }
		});
	}
	
	public void validateSpinner1Selection(Spinner spinner1, Spinner spinner2, int minimum, int maximum) {
		int selection1 = findSelectedValue(spinner1);
		int selection2 = findSelectedValue(spinner2);
		
		if( selection1 < minimum ){
			this.updateSpinnerSelection(spinner1, minimum);
		}else if(selection1 > selection2){
			this.updateSpinnerSelection(spinner1, selection2);
		}
	}
	
	public void validateSpinner2Selection(Spinner spinner1, Spinner spinner2, int minimum, int maximum) {
		int selection1 = findSelectedValue(spinner1);
		int selection2 = findSelectedValue(spinner2);
		
		if( selection2 < selection1){
			this.updateSpinnerSelection(spinner2, selection1);
		}else if(selection2 > maximum){
			this.updateSpinnerSelection(spinner2, selection1);
		}
	}
	
	public int findSelectedMeasure1(View view) {
		return this.findSelectedValue((Spinner) view.findViewById(R.id.measure_clean_dlg_from_value));
	}
	
	public int findSelectedMeasure2(View view) {
		return this.findSelectedValue((Spinner) view.findViewById(R.id.measure_clean_dlg_to_value));
	}
	
	public int findSelectedValue(Spinner spinner) {
		return ((Integer) ((SelectableItem)spinner.getSelectedItem()).getItem()).intValue();
	}
	
	@SuppressWarnings("unchecked")
	public void updateSpinnerSelection(Spinner spinner, int selection) {
		ArrayAdapter<SelectableItem> adapter = (ArrayAdapter<SelectableItem>) spinner.getAdapter();
		spinner.setSelection(adapter.getPosition(new SelectableItem(Integer.valueOf(selection), null)), false);
	}
	
	public void processAction(TGTrack track, Integer measure1, Integer measure2) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(findContext(), TGCleanMeasureListAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, track);
		tgActionProcessor.setAttribute(TGCleanMeasureListAction.ATTRIBUTE_MEASURE_NUMBER_1, measure1);
		tgActionProcessor.setAttribute(TGCleanMeasureListAction.ATTRIBUTE_MEASURE_NUMBER_2, measure2);
		tgActionProcessor.process();
	}
}
