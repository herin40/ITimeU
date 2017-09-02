package com.itti7.itimeu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class AboutFragment extends Fragment {

    // This application's Version
    final static String APPLICATION_VERSION = "0.0.0";

    // Mail receiver
    String[] emailTo = { "1117hyemin@gmail.com" , "juneoh227@gmail.com"
            , "lync2846@gmail.com"};

    // Mail title subject
    String emailSubject = "[I Time U]";

    // This Fragment's inflater, activity, context
    View mAboutView;
    Activity mAboutActivity;
    Context mAboutContext;

    // TextView for setting version text
    TextView mVersionTextView;
    // TextView to show Activity for open licenses
    TextView mLicensesTextView;
    // TextView for feedback with mail
    TextView mFeedbackTextView;

    public AboutFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        mAboutView = inflater.inflate(R.layout.fragment_about, container, false);
        mAboutActivity = getActivity();
        mAboutContext = mAboutView.getContext();

        // Get Version TextView for setting text
        mVersionTextView = mAboutView.findViewById(R.id.version_txt_view);
        mVersionTextView.setText(APPLICATION_VERSION);

        // When click this text view, than start an activity which show licenses's info
        mLicensesTextView = mAboutView.findViewById(R.id.licenses_txt_view);
        mLicensesTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mAboutContext, LicensesActivity.class);
                startActivity(intent);
            }
        });

        // When click this text view, than show Email intent for feedback
        mFeedbackTextView = mAboutView.findViewById(R.id.feedback_txt_view);
        mFeedbackTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendFeedback(emailTo, emailSubject);
            }
        });
        return mAboutView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    /**
     * This function create intent for sending Email.
     *
     * @param mailto    Receiving address
     * @param subject   Subject of mail
     */
    public void sendFeedback(String[] mailto, String subject) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Intent.EXTRA_EMAIL, mailto);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);

        startActivity(intent);
    }
}
