package br.edu.ifpb.breath.fragments;

/**
 * This interface implements a fragment that handles refresh task.
 *
 * @author Felipe Porge Xavier - http://www.felipeporge.com
 */
public interface RefreshHandlerFragment {

    /**
     * Called when the fragment needs to refresh.
     * @param extra - Extras.
     */
    void refresh(Object extra);
}
