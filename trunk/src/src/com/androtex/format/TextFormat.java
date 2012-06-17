package com.androtex.format;

import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.widget.EditText;

/**
 * Class used to manage a tex source file and show corresponding colors
 * 
 * @author Kevin Le Perf
 * @see TextWatcher
 * 
 */
public class TextFormat implements TextWatcher {
	private EditText _text;

	public TextFormat(EditText text) {
		_text = text;
	}

	@Override
	public void afterTextChanged(Editable paramEditable) {
		if(_text.getSelectionStart() != -1){
			_text.removeTextChangedListener(this);
			// we will write it so we disable the listening !
			// if not > infinite loop :p
			setSpanBetweenTokens(paramEditable);
			_text.addTextChangedListener(this);
		}
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}

	/**
	 * match do we have the <b>expr</b> string at the index <b>i</b> of
	 * <b>text</b>
	 * 
	 * @param text
	 *            the text
	 * @param i
	 *            the index
	 * @param expr
	 *            the substring
	 * @return a boolean indicating true or false
	 */
	public static boolean match(Editable text, int i, String expr) {
		boolean ok = true;
		for (int k = 0; i + k + expr.length() < text.length()
				&& k < expr.length() && ok; k++) {
			Log.d("indice",
					"" + k + " " + text.charAt(i + k) + " " + expr.charAt(k));
			ok = text.charAt(i + k) == expr.charAt(k);
		}

		return ok;
	}

	private int _modif;
	private final static int MAX=20; 
	public CharSequence setSpanBetweenTokens(Editable text) {
		int st = _text.getSelectionStart();
		int debut = st;
		int fin = st;
		for(;debut >= 0 && debut<text.length() && text.charAt(debut) != '\n';debut--);
		for(;fin<_text.length()-1 && text.charAt(fin) != '\n';fin++);
		//bug fix
		//TODO fix it without this fin=text.length()-1
		//TODO if length = 0 > manage and create etc...
		fin = text.length()-1;
		if(debut != -1 && fin != -1){
			ForegroundColorSpan [] _f = text.getSpans(debut, fin, ForegroundColorSpan.class);
			if(_f.length != 0){
				for(int i=0;i<_f.length;i++){
					text.removeSpan(_f[i]);
				}
			}
		}
		
		Editable _spans = text;

		int i = 0;
		int j = 0;
		while (i>= 0 && i < text.length()) {
			if (text.charAt(i) == '%') {
				for (j = i + 1; j < text.length() && text.charAt(j) != '\n'; j++)
					;
				_spans.setSpan(new ForegroundColorSpan(0xffaaaaaa), i, j, 0);

				i = j;
			} else if (text.charAt(i) == '\\') {
				for (j = i + 1; j < text.length()
						&& (text.charAt(j) != '\n' && text.charAt(j) != ' '
						&& text.charAt(j) != '{' && text.charAt(j) != '['); j++)
					;
				_spans.setSpan(new ForegroundColorSpan(0xff0336de), i, j, 0);
				i = j;
			} else if (text.charAt(i) == '{') { // autorise text.charAt(j)!=' '
				// &&
				for (j = i + 1; j < text.length()
						&& (text.charAt(j) != '\n' && text.charAt(j) != '}'
						&& text.charAt(j) != '{' && text.charAt(j) != '['); j++)
					;

				if (j < text.length() && text.charAt(j) == '}') // add } in the
					// span
					j++;
				_spans.setSpan(new ForegroundColorSpan(0xff8000ff), i, j, 0);
				i = j;
			} else if (text.charAt(i) == '[') { // autorise text.charAt(j)!=' '
				// &&
				for (j = i + 1; j < text.length()
						&& (text.charAt(j) != '\n' && text.charAt(j) != ' '
						&& text.charAt(j) != ']'
						&& text.charAt(j) != '{' && text.charAt(j) != '['); j++)
					;

				if (j < text.length() && text.charAt(j) == ']') // add ] in the
					// span
					j++;
				_spans.setSpan(new ForegroundColorSpan(0xfff38125), i, j, 0);
				i = j;
			} else {
				i++;
			}
		}

		/* formattage particulier de chaines et groupes */
		i = 0;
		String textbf = "textbf";
		String textit = "textit";

		while (i < text.length()) {
			if (i < text.length() && text.charAt(i) == '%') {
				for (j = i + 1; j < text.length() && text.charAt(j) != '\n'; j++)
					;
				i = j;
			}
			if (i < text.length() && text.charAt(i) == '\\') {
				i++;
				if (i + textbf.length() < text.length()
						&& match(text, i, textbf)) {
					Log.d("okokok", "okokok");
					int s = i + 7;
					int e = s;
					for (; e < text.length() && text.charAt(e) != '\n'
							&& text.charAt(e) != ']' && text.charAt(e) != '}'; e++)
						;

					try {
						_spans.setSpan(new StyleSpan(Typeface.BOLD), s, e, 0);
						if (e + 1 < text.length())
							_spans.setSpan(new StyleSpan(Typeface.NORMAL),
									e + 1, text.length() - 1, 0);
					} catch (Exception exc) {

					}
					i = e;
				} else if (i + textit.length() < text.length()
						&& match(text, i, textit)) {
					int s = i + 7;
					int e = s;
					for (; e < text.length() && text.charAt(e) != '\n'
							&& text.charAt(e) != ']' && text.charAt(e) != '}'; e++)
						;
					try {
						_spans.setSpan(new StyleSpan(Typeface.ITALIC), s, e, 0);
						if (e + 1 < text.length())
							_spans.setSpan(new StyleSpan(Typeface.NORMAL),
									e + 1, text.length() - 1, 0);
					} catch (Exception exc) {

					}
					i = e;
				} else {
					i++;
				}
			} else
				i++;
		}

		return _spans;
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
	}

}
