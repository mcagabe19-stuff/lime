#include "../../SDL_internal.h"

#ifdef SDL_VIDEO_DRIVER_UIKIT

#include "SDL.h"
#include "SDL_uikitvideo.h"
#include "SDL_uikitwindow.h"

/* Display a UIKit message box */

static SDL_bool s_showingMessageBox = SDL_FALSE;

SDL_bool UIKit_ShowingMessageBox(void)
{
    return s_showingMessageBox;
}

static void UIKit_ShowMessageBoxImpl(const char *title, const char *message)
{
    NSLog(@"Message Box");
    NSLog(@"Title: %@", @(title));
    NSLog(@"Message: %@", @(message));
    NSLog(@"Message Box End");
}

int UIKit_ShowMessageBox(const char *title, const char *message)
{
    UIKit_ShowMessageBoxImpl(title, message);
    return 0;
}

#endif /* SDL_VIDEO_DRIVER_UIKIT */
