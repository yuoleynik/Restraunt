package manager;

import process.ProcessAgent;
import simulation.Simulation;
import visitor.VisitorAgent;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;


public class OrderAgent implements Runnable {
    Simulation simulation;
    visitor.VisitorAgent customer;
    ArrayList<MenuDish> orderedDishes;
    ArrayList<DishCard> dishCards = new ArrayList<>();;
    ArrayList<Future<?>> processes = new ArrayList<>();
    private LocalDateTime timeStart;
    private LocalDateTime timeEnd;
    boolean done = false;

    OrderAgent(Simulation simulation, VisitorAgent customer, ArrayList<MenuDish> orderedDishes) {
        this.customer = customer;
        this.simulation = simulation;
        this.orderedDishes = orderedDishes;
    }

    @Override
    public void run() {
        timeStart = simulation.getCurrentTime();
        Handbook handbook = simulation.getRestaurant().getManager().provideHandBook();
        for (var menuDish : orderedDishes) {
            var dishCard = handbook.getDishCardByMenuId(menuDish.getMenuDishId());
            dishCards.add(dishCard);
            var process = new ProcessAgent(simulation.getRestaurant().getManager().getProcessId(),
                                           simulation, dishCard, menuDish.getMenuDishId());
            processes.add(simulation.submit(process));
        }
        for (var future : processes) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        timeEnd = simulation.getCurrentTime();
        done = true;
        log();
    }

    public boolean isDone() {
        return done;
    }

    void log() {
        // TODO
    }
}
