package live.labaguettedev.mytransfer.presenters;

import live.labaguettedev.mytransfer.model.SelectedFile;

public class FilePresenter {

    private SelectedFile file;
    ISelectedFileScreen selectedFileScreen;

    public interface ISelectedFileScreen {
        void showSelectedFile(String path, String name, double size);
    }

    public FilePresenter(ISelectedFileScreen selectedFileScreen) {
        this.selectedFileScreen = selectedFileScreen;
        this.file = null;
    }


    public void setSelectedFile(String path, String name, double size) {
        this.file = new SelectedFile(path, name, size);

        selectedFileScreen.showSelectedFile(path, name, size);
    }

}
