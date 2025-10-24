package com.example.expense_management.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.expense_management.R;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.button.MaterialButton;

public class DashBoard extends Fragment {
    public DashBoard() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate layout dashboard.xml
        View view = inflater.inflate(R.layout.dashboard, container, false);

        // 🔹 Bắt sự kiện click đơn giản để test điều hướng (tùy chọn)
        MaterialCardView cardExpense = view.findViewById(R.id.cardExpense);
        MaterialCardView cardTarget = view.findViewById(R.id.cardTarget);
        MaterialButton analyticBtn = view.findViewById(R.id.analyticBtn);
        MaterialButton plotBtn = view.findViewById(R.id.plotBtn);

//        cardExpense.setOnClickListener(v ->
//                startActivity(new Intent(requireContext(), ExpenseDetails.class)));
//
//        cardTarget.setOnClickListener(v ->
//                startActivity(new Intent(requireContext(), TitleDetail.class)));
//
//        analyticBtn.setOnClickListener(v ->
//                startActivity(new Intent(requireContext(), ExpenseSuggestion.class)));
//
//        plotBtn.setOnClickListener(v ->
//                startActivity(new Intent(requireContext(), ExpenseAnalyst.class)));

        return view;
    }
}
