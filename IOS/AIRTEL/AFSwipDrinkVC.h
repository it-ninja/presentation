//
//  AFSwipDrinkVC.h
//  Unliminet
//
//  Created by Apple on 23/03/15.
//  Copyright (c) 2015 Kleward. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "AFRedeemPopUP.h"
#import "AFSucessPopUp.h"

@interface AFSwipDrinkVC : UIViewController<UIGestureRecognizerDelegate>{
    float moveX;
    float moveY;
    IBOutlet UILabel *partyNameLabel;
    IBOutlet UILabel *swipeToDrinkL;
    IBOutlet UILabel *swipeUpwordL;
    IBOutlet UIButton *pointUpButton;
    IBOutlet UIButton *pointDownButton;


}

@end
