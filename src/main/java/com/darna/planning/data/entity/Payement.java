package com.darna.planning.data.entity;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity(name = "payement")
public class Payement extends AbstractEntity {

    // la personne qui a efféctué le versement
    @Column(name = "source")
    private String source;
    // l'entité cible
    @Column(name = "payement_target")
    private String target;
    // mantant payé
    @Column(name = "amount")
    private String amount;
    // montant prévu pour le payement
    @Column(name = "goal")
    private String goal;
    // date de virement
    @Column(name = "payement_date")
    private LocalDate date;
    // le reste du totale
    @Column(name = "remaining")
    private String remaining;
    // l'objectif de paiement a-t-il été atteint?
    @Column(name = "reached")
    private boolean reached;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getRemaining() {
        return remaining;
    }

    public void setRemaining(String remaining) {
        this.remaining = remaining;
    }

    public boolean isReached() {
        return reached;
    }

    public void setReached(boolean reached) {
        this.reached = reached;
    }

}
