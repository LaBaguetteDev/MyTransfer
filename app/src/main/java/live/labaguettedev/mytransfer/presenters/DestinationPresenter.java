package live.labaguettedev.mytransfer.presenters;

import live.labaguettedev.mytransfer.model.Destination;

public class DestinationPresenter {

    private Destination destination;
    IDestinationScreen destinationScreen;

    public interface IDestinationScreen {
        void showAllDestination(String type, String name);
    }

    public DestinationPresenter(IDestinationScreen destinationScreen) {
        this.destinationScreen = destinationScreen;
        this.destination = null;
    }

    

}
