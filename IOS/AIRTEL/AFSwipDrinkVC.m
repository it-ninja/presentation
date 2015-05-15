//
//  AFSwipDrinkVC.m
//  Unliminet
//
//  Created by Apple on 23/03/15.
//  Copyright (c) 2015 Kleward. All rights reserved.
//

#import "AFSwipDrinkVC.h"
#import "DrinkInfoHTTPClientAPI.h"


#import "AFFriendVC.h"
#import "UIViewController+ENPopUp.h"
#import "UIViewController+CWPopup.h"
#import "AFInvitePopUp.h"

#import "AFBarVC.h"
#import "AFSwipePopupVC.h"
#import "SDrinkWBarHTTPClientAPI.h"

#define MARBLE_WIDTH 96-20
#define MARBLE_HEIGHT 116-20

@interface AFSwipDrinkVC ()<UIGestureRecognizerDelegate,DrinkInfoHTTPClientAPIDelegate,SDrinkWBarHTTPClientAPIDelegate>{
    
    IBOutlet UIView *swipeView;
    IBOutlet UIButton *homeButton;
    
    IBOutlet UILabel *pointLabel1;
    IBOutlet UILabel *pointLabel2;
    IBOutlet UILabel *pointLabel3;
    IBOutlet UILabel *pointLabel4;
    int number;
    NSString *drink_count;
    
    UIView *bollView;
    
    BOOL isSwipeFirstFriend;
    BOOL isSwipeFirstBar;
    NSString *canSwipeBar;
    NSString *canSwipeFriend;
    
    
    
}

@property (nonatomic,strong)AFFriendVC *friendVC;

@end


@implementation AFSwipDrinkVC



- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}



// on drink increment
-(IBAction)incrementNumber:(id)sender{
    
    
    if ([drink_count intValue] == 0 ) {
        
        NSString *message = @"You don't have any drinks.";
        [self alerMassege:message];
        return;
    }
    
    if (number == [drink_count intValue] ) {
        
        NSString *message = @"You already reached your maximum limit";
        [self alerMassege:message];
        return;
    }
    
    number++;
	[pointLabel4 setText:[NSString stringWithFormat:@"%d", number]];
    NSString *drink_counts       =  [NSString stringWithFormat:@"%d", number];
    if (drink_counts.length==1) {
        drink_counts =[NSString stringWithFormat:@"%@%@",@"0",drink_counts];
    }
    
    
    NSString *point =drink_counts;
    NSMutableArray *pointArray = [NSMutableArray array];
    for (int i=0; i<point.length; i++) {
        [pointArray addObject:[point substringWithRange:NSMakeRange(i, 1)]];
    }
    
    pointLabel3.text = [pointArray objectAtIndex:0];
    pointLabel4.text = [pointArray objectAtIndex:1];
    [self updatePoints];
    
    
}

// on drink decrement
-(IBAction)decrementNumber:(id)sender{
    if (number==0) {
        NSString *message = @"At least select one drink.";
        [self alerMassege:message];
        return;
        
    }
    number--;
	[pointLabel4 setText:[NSString stringWithFormat:@"%d", number]];
    
    NSString *drink_counts       =  [NSString stringWithFormat:@"%d", number];
    
    if (drink_counts.length==1) {
        drink_counts =[NSString stringWithFormat:@"%@%@",@"0",drink_counts];
    }
    
    
    NSString *point =drink_counts;
    
    NSMutableArray *pointArray = [NSMutableArray array];
    for (int i=0; i<point.length; i++) {
        [pointArray addObject:[point substringWithRange:NSMakeRange(i, 1)]];
    }
    
    pointLabel3.text = [pointArray objectAtIndex:0];
    pointLabel4.text = [pointArray objectAtIndex:1];
    
    [self updatePoints];
    
}


//Update drink points
-(void)updatePoints{
    int i = [drink_count intValue] - number;
    NSString *drink_counts = [NSString stringWithFormat:@"%d",i];;
    if (drink_counts.length==1) {
        drink_counts =[NSString stringWithFormat:@"%@%@",@"0",drink_counts];
    }
    NSString *point =drink_counts;
    NSMutableArray *pointArray = [NSMutableArray array];
    for (int i=0; i<point.length; i++) {
        [pointArray addObject:[point substringWithRange:NSMakeRange(i, 1)]];
    }
    
    pointLabel1.text = [pointArray objectAtIndex:0];
    pointLabel2.text = [pointArray objectAtIndex:1];
    
}



// swipe Drink on bar side
-(void)AFBarVC:(id)sender{
    [self swipeDrinkWithBar:[NSString stringWithFormat:@"%d",number]];
}


// swipe Drink on pals side
-(void)friendVC:(id)sender{
    AFFriendVC *friend = [[AFFriendVC alloc] initWithNibName:@"AFFriendVC" bundle:nil];
    friend.drinkCount = [NSString stringWithFormat:@"%d",number];
    [self presentPopupViewController:friend animated:YES completion:^(void) {
        NSLog(@"popup view presented");
    }];
    
}

#pragma mark - Popup Functions
- (void)dismissPopup:(id)sender {
    if (self.popupViewController != nil) {
        [self dismissPopupViewControllerAnimated:YES completion:^{
            NSLog(@"popup view dismissed");
            drink_count = [NSString stringWithFormat:@"%@%@", pointLabel1.text,pointLabel2.text];
            number = 0;
            [pointLabel3 setText:[NSString stringWithFormat:@"%d", number]];
            [pointLabel4 setText:[NSString stringWithFormat:@"%d", number]];
            [self updatePoints];
            
            
            // Recreate ball view
            
            bollView = [[UIView alloc] initWithFrame:CGRectMake(125, 220+60, MARBLE_WIDTH, MARBLE_HEIGHT)];
            bollView.backgroundColor = [UIColor clearColor];
            bollView.userInteractionEnabled = YES;
            bollView.layer.cornerRadius = 100/2;
            [self.view addSubview:bollView];
            
            
            UIImageView *boll = [[UIImageView alloc] initWithFrame:CGRectMake(0, 0, MARBLE_WIDTH, MARBLE_HEIGHT)];
            boll.userInteractionEnabled = NO;
            
            boll.image = [UIImage imageNamed:@"Drink-to-swipe"];
            [bollView addSubview:boll];
            
            
            CGAffineTransform transform = CGAffineTransformMakeRotation(M_PI/12);
            [bollView setTransform:transform];
            UISwipeGestureRecognizer *swipeLeftGesture =
            [[UISwipeGestureRecognizer alloc]initWithTarget:self action:@selector(swipeLeft:)];
            swipeLeftGesture.direction = UISwipeGestureRecognizerDirectionLeft;
            swipeLeftGesture.delegate = self;
            [bollView addGestureRecognizer:swipeLeftGesture];
            
            UISwipeGestureRecognizer *swipeUpwardGesture =
            [[UISwipeGestureRecognizer alloc]initWithTarget:self action:@selector(swipeUpward:)];
            swipeUpwardGesture.direction = UISwipeGestureRecognizerDirectionUp;
            swipeUpwardGesture.delegate = self;
            
            [bollView addGestureRecognizer:swipeUpwardGesture];
            
            
            
        }];
    }
}



-(IBAction)back:(id)sender{
    [self.navigationController popViewControllerAnimated:YES];
}



// receive swipe notification
-(void)swipeToPals:(id)sender{
    isSwipeFirstFriend  = NO;
    [self friendVC:nil];
    
}
// receive swipe notification
-(void)swipeToBar:(id)sender{
    isSwipeFirstBar  = NO;
    [self AFBarVC:nil];
}


- (void)viewDidLoad
{
    [super viewDidLoad];
    number = 0;
    
    // Change subcomponent frame
    if (IS_IPHONE_4_OR_LESS) {
    }else{
        swipeToDrinkL.frame = CGRectMake(100, 456, 69, 39);
        swipeUpwordL.frame = CGRectMake(57, 508, 206, 49);
        pointUpButton.frame = CGRectMake(226, 439, 61, 32);
        pointDownButton.frame = CGRectMake(229, 479, 54, 41);
        pointLabel3.frame = CGRectMake(180, 461, 30, 30);;
        pointLabel4.frame = CGRectMake(213, 461, 30, 30);
    }
    
    partyNameLabel.text = [UserPreference getPartyName];
    [homeButton addTarget:(AFONavigationController *)self.navigationController action:@selector(showMenu) forControlEvents:UIControlEventTouchUpInside];
    
    // Add Notification
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(dismissPopup:)
                                                 name:@"dismissPopup"
                                               object:nil];
    
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(swipeToPals:)
                                                 name:@"swipeToPals"
                                               object:nil];
    
    
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(swipeToBar:)
                                                 name:@"swipeToBar"
                                               object:nil];
    
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(dismissPopupSwipeVC:)
                                                 name:@"dismissPopupSwipeVC"
                                               object:nil];
    
    
    
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(dismissPopup:)
                                                 name:@"dismissPopupFriendVC"
                                               object:nil];
    
    
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(dismissPopupRedeemVC:)
                                                 name:@"dismissPopupRedeemVC"
                                               object:nil];
    
    
    //
    
    self.useBlurForPopup = NO;
    
    //Check drink info

    [self getDrinkInfoFromServer];
    
    //Add boll object on viewload
    bollView = [[UIView alloc] initWithFrame:CGRectMake(125, 220+60, MARBLE_WIDTH, MARBLE_HEIGHT)];
    bollView.backgroundColor = [UIColor clearColor];
    
    
    bollView.userInteractionEnabled = YES;
    bollView.layer.cornerRadius = 100/2;
    [self.view addSubview:bollView];
    
    
    UIImageView *boll = [[UIImageView alloc] initWithFrame:CGRectMake(0, 0, MARBLE_WIDTH, MARBLE_HEIGHT)];
    
    boll.image = [UIImage imageNamed:@"Drink-to-swipe"];
    boll.userInteractionEnabled = NO;
    
    [bollView addSubview:boll];
    
    
    CGAffineTransform transform = CGAffineTransformMakeRotation(M_PI/12);
    [bollView setTransform:transform];
    

    //Add Swipe Gesture
    UISwipeGestureRecognizer *swipeLeftGesture =
    [[UISwipeGestureRecognizer alloc]initWithTarget:self action:@selector(swipeLeft:)];
    swipeLeftGesture.direction = UISwipeGestureRecognizerDirectionLeft;
    swipeLeftGesture.delegate = self;
    [bollView addGestureRecognizer:swipeLeftGesture];
    
    UISwipeGestureRecognizer *swipeUpwardGesture =
    [[UISwipeGestureRecognizer alloc]initWithTarget:self action:@selector(swipeUpward:)];
    swipeUpwardGesture.direction = UISwipeGestureRecognizerDirectionUp;
    swipeUpwardGesture.delegate = self;
    
    [bollView addGestureRecognizer:swipeUpwardGesture];
    
    
}



#pragma mark - Gesture Delegate

- (void)swipeUpward:(UISwipeGestureRecognizer *)sender
{
    //canSwipeFriend
    
    if (number==0) {
        NSString *message = @"You need to select at least one drink to swipe.";
        [self alerMassege:message];
        return;
    }
    
    
    
    if ([canSwipeFriend isEqualToString:@"no"]) {
        NSString *message = @"You can't swipe drinks to pal before ticket announcement date";
        [self alerMassege:message];
        return;
    }
    
    
    
    [UIView animateWithDuration:1.5
                          delay:0
                        options: UIViewAnimationOptionCurveEaseInOut
                     animations:^{
                         
                         sender.view.frame = CGRectMake(sender.view.frame.origin.y-20, 90, 90, 169);
                         
                     }
                     completion:^(BOOL finished){
                         
                         [[NSNotificationCenter defaultCenter] postNotificationName:@"swipeToPals" object:self];
                         sender.view.hidden = YES;
                         [sender.view removeFromSuperview];
                         
                         
                     }];
    
    
}

// Show Custom popup
-(void)swipeFirstAlerMassege:(NSDictionary *)dict{
    
    AFSwipePopupVC *vc = [[AFSwipePopupVC alloc]initWithNibName:@"AFSwipePopupVC" bundle:nil];
    vc.alertDict = dict;
    vc.view.frame = CGRectMake(0, 0, 200, 275);
    [self presentPopupViewController:vc animated:YES completion:^(void) {
        NSLog(@"popup view presented");
    }];
    
}

#pragma mark - Popup Functions
- (void)dismissPopupSwipeVC:(NSNotification *)notification {
    UIButton *btn = (UIButton *)notification.object;
    
    if (btn.tag==1) {
        isSwipeFirstFriend = YES;
    }
    
    if (btn.tag==2) {
        isSwipeFirstBar = YES;
    }
    
    if (self.popupViewController != nil) {
        
        [self dismissPopupViewControllerAnimated:YES completion:^{
            NSLog(@"popup view dismissed");
            
            drink_count = [NSString stringWithFormat:@"%@%@", pointLabel1.text,pointLabel1.text];
            number = 0;
            [pointLabel3 setText:[NSString stringWithFormat:@"%d", number]];
            [pointLabel4 setText:[NSString stringWithFormat:@"%d", number]];
            
            
        }];
    }
}


- (void)dismissPopupRedeemVC:(NSNotification *)notification {
    if (self.popupViewController != nil) {
        [self dismissPopupViewControllerAnimated:YES completion:^{
            NSLog(@"popup dismissPopupRedeemVC dismissed");
            [UIView animateWithDuration:1.5
                                  delay:0
                                options: UIViewAnimationOptionCurveEaseInOut
                             animations:^{
                                 
                                 bollView.frame = CGRectMake(bollView.frame.origin.x-60, 90, 90, 169);
                                 
                             }
                             completion:^(BOOL finished){
                                 NSLog(@"swipeToBar!");
                                 
                                 [[NSNotificationCenter defaultCenter] postNotificationName:@"swipeToBar" object:self];
                                 bollView.hidden = YES;
                                 [bollView removeFromSuperview];
                                 
                             }];
            
            
            
        }];
    }
}



- (void)swipeLeft:(UISwipeGestureRecognizer *)sender
{

    //Check drink is selected or not
    if (number==0) {
        NSString *message = @"You need to select at least one drink to swipe.";
        [self alerMassege:message];
        return;
    }
    
    // Check validataion for redeem drinks from bar before party date
    if ([canSwipeBar isEqualToString:@"no"] ){
        NSString *message = @"You can't redeem drinks from bar before party date";
        [self alerMassege:message];
        return;
        
    }

    
    // Add custom pop
    if (isSwipeFirstBar == NO){
        NSDictionary *dict = [[NSDictionary alloc]initWithObjectsAndKeys:@"Easy now ...",@"easy_now",@"You're about to redeem a drink from the bar!",@"aleart_title",@"Do you wish to continue?",@"continue_text",@"Redeem",@"button_title",@"0",@"isFriend", nil];
        
        AFRedeemPopUP *vc = [[AFRedeemPopUP alloc]initWithNibName:@"AFRedeemPopUP" bundle:nil];
        vc.alertDict = dict;
        vc.view.frame = CGRectMake(0, 0, 200, 275);
        [self presentPopupViewController:vc animated:YES completion:^(void) {
            NSLog(@"popup view presented");
        }];
        return;
        
    }
    
    [UIView animateWithDuration:1.5
                          delay:0
                        options: UIViewAnimationOptionCurveEaseInOut
                     animations:^{
                         
                         sender.view.frame = CGRectMake(sender.view.frame.origin.x-60, 90, 90, 169);
                         
                     }
                     completion:^(BOOL finished){
                         NSLog(@"swipeToBar!");
                         
                         [[NSNotificationCenter defaultCenter] postNotificationName:@"swipeToBar" object:self];
                         
                         
                         sender.view.hidden = YES;
                         
                         [sender.view removeFromSuperview];
                         
                     }];
    
    
    
}




// Add bollview //  Not use
-(void)showView:(id)sender{
    
    UIView *view = (UIView *)sender;
    view.frame =  CGRectMake(160, 220+90, 100, 100);
    view.hidden = NO;
    
    view = [[UIView alloc] initWithFrame:CGRectMake(120, 220+90, MARBLE_WIDTH, MARBLE_HEIGHT)];
    view.backgroundColor = [UIColor clearColor];
    
    
    view.userInteractionEnabled = YES;
    view.layer.cornerRadius = 100/2;
    //    [self.view addSubview:view];
    
    
    UIImageView *boll = [[UIImageView alloc] initWithFrame:CGRectMake(0, 0, 96/2, 116/2)];
    boll.image = [UIImage imageNamed:@"Drink-to-swipe"];
    [view addSubview:boll];
    
    
    CGAffineTransform transform = CGAffineTransformMakeRotation(M_PI/4);
    [view setTransform:transform];
    
    UISwipeGestureRecognizer *swipeLeftGesture =
    [[UISwipeGestureRecognizer alloc]initWithTarget:self action:@selector(swipeLeft:)];
    swipeLeftGesture.direction = UISwipeGestureRecognizerDirectionLeft;
    swipeLeftGesture.delegate = self;
    [view addGestureRecognizer:swipeLeftGesture];
    
    UISwipeGestureRecognizer *swipeUpwardGesture =
    [[UISwipeGestureRecognizer alloc]initWithTarget:self action:@selector(swipeUpward:)];
    swipeUpwardGesture.direction = UISwipeGestureRecognizerDirectionUp;
    swipeUpwardGesture.delegate = self;
    
    [view addGestureRecognizer:swipeUpwardGesture];
    
    
}



#pragma mark - Touches Event Delegate Functions

-(void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event{
    
}

#pragma mark - Gesture Delegate Functions

- (BOOL)gestureRecognizerShouldBegin:(UIGestureRecognizer *)gestureRecognizer{
    return YES;
}

- (BOOL)gestureRecognizer:(UIGestureRecognizer *)gestureRecognizer shouldRecognizeSimultaneouslyWithGestureRecognizer:(UIGestureRecognizer *)otherGestureRecognizer{
    return YES;
}
- (BOOL)gestureRecognizer:(UIGestureRecognizer *)gestureRecognizer shouldReceiveTouch:(UITouch *)touch{
    return YES;
}



/* *************************< Update Notification On Server >*************************** */




-(void)getDrinkInfoFromServer{
    /*
     
     X_REST_PASSWORD	pAirtelSocialParty	Application password.
     user_id*	1	User id provided at the time of login.
     list.
     
     */
    
    [SVProgressHUD show];
    
    NSMutableDictionary *parameters = [NSMutableDictionary dictionary];
    
    parameters[@"X_REST_USERNAME"]      = [UserPreference getScreteAPIKey];
    parameters[@"X_REST_PASSWORD"]      = [UserPreference getScreteAPIPassword];
    parameters[@"user_id"]              = [UserPreference getUserID];
    parameters[@"party_key"]              = [UserPreference getPartyKey];
    parameters[@"session_token"]            = [UserPreference getSessionToken];

    
    DrinkInfoHTTPClientAPI * drinkInfoHTTPClientAPI = [DrinkInfoHTTPClientAPI sharedDrinkInfoAPIHTTPClient];
    drinkInfoHTTPClientAPI.delegate = self;
    [drinkInfoHTTPClientAPI getDrinkInfo:parameters] ;
    
}


-(void)drinkInfoHTTPClientAPI:(DrinkInfoHTTPClientAPI *)client didSuccess:(id)response{
    
    [SVProgressHUD dismiss];
    
    NSDictionary *responceDict = (NSDictionary *)response;
    NSLog(@"responceDict %@",responceDict);
    // get the status for the Success

    BOOL isSuccess = [[responceDict objectForKey:@"status"]boolValue];
    
    if (isSuccess==YES) {
        
        NSDictionary *resultDict    = [responceDict objectForKey:@"result"];
        drink_count       =  [resultDict objectForKey:@"drink_count"];
        
        canSwipeFriend   =  [resultDict objectForKey:@"can_swipe"];
        canSwipeBar   =  [resultDict objectForKey:@"can_swipe_from_bar"];
        
        if (drink_count.length==1) {
            drink_count =[NSString stringWithFormat:@"%@%@",@"0",drink_count];
        }
        
        
        NSString *point =drink_count;
        
        NSMutableArray *pointArray = [NSMutableArray array];
        for (int i=0; i<point.length; i++) {
            [pointArray addObject:[point substringWithRange:NSMakeRange(i, 1)]];
        }
        
        
        pointLabel1.text = [pointArray objectAtIndex:0];
        pointLabel2.text = [pointArray objectAtIndex:1];
        
        
        
    }else{
        
        // Show the user an error message

        if ([[responceDict objectForKey:@"message"] isEqualToString:@"user_not_exist"]) {
            [UserPreference alerMassegeWithLogout:responceDict];
        }
        else if ([[responceDict objectForKey:@"message"] isEqualToString:@"user_inactive"]) {
            [UserPreference alerMassegeWithLogout:responceDict];
        }
        else if ([[responceDict objectForKey:@"message"] isEqualToString:@"session_expired"]) {
            [UserPreference alerMassegeWithLogout:responceDict];
        }else{
        NSString *message = [responceDict objectForKey:@"message"];
        [self alerMassege:message];
        
        }
    }
    
    
}

// An error occurred, we need to handle the error
-(void)drinkInfoHTTPClientAPI:(DrinkInfoHTTPClientAPI *)client didFailWithError:(NSError *)error{
    
    [SVProgressHUD dismiss];
}


// Add custom popup
-(void)alerMassege:(NSString *)massege{
    
    AFAlerPopupVC *vc = [[AFAlerPopupVC alloc]initWithNibName:@"AFAlerPopupVC" bundle:nil];
    vc.alertStringTitle = massege;
    vc.view.frame = CGRectMake(0, 0, 200, 245);
    [self presentPopUpViewController:vc];
    
}




/* *************************< Share Drink With Bar >*************************** */


-(void)swipeDrinkWithBar:(NSString *)drinkCount{
    
    /*
     X_REST_USERNAME	airtelSocialParty	Application user name
     X_REST_PASSWORD	pAirtelSocialParty	Application password.
     user_id*	1	User id provided at the time of login.
     drink_count*	2	No of drinks you want to swipe.
     */
    
    [SVProgressHUD show];
    
    NSMutableDictionary *parameters = [NSMutableDictionary dictionary];
    
    parameters[@"X_REST_USERNAME"]      = [UserPreference getScreteAPIKey];
    parameters[@"X_REST_PASSWORD"]      = [UserPreference getScreteAPIPassword];
    parameters[@"user_id"]              = [UserPreference getUserID];
    parameters[@"session_token"]            = [UserPreference getSessionToken];
    parameters[@"party_key"]            = [UserPreference getPartyKey];

    parameters[@"drink_count"]          = drinkCount;
    NSLog(@"parameters %@",parameters);
    
    SDrinkWBarHTTPClientAPI * drinkWBarHTTPClientAPI = [SDrinkWBarHTTPClientAPI sharedSDrinkWBarAPIHTTPClient];
    drinkWBarHTTPClientAPI.delegate = self;
    [drinkWBarHTTPClientAPI shareDrinkWithBar:parameters] ;
    
}


-(void)sDrinkWBarHTTPClientAPI:(SDrinkWBarHTTPClientAPI *)client didSuccess:(id)response{
    
    [SVProgressHUD dismiss];
    
    NSDictionary *responceDict = (NSDictionary *)response;
    NSLog(@"responceDict %@",responceDict);
    
    
    BOOL isSuccess = [[responceDict objectForKey:@"status"]boolValue];
    
    if (isSuccess==YES) {
        
        [self initBolls];
        
        
        NSDictionary *resultDict = [responceDict objectForKey:@"result"];
        
        /*
         apiName = AspRedeemDrinksFromBar;
         message = "Your request for drinks redemption has been sent to the bar.";
         result =     {
         "drink_count" = 1;
         };
         status = true;
         */
        
        drink_count = [NSString stringWithFormat:@"%@%@", pointLabel1.text,pointLabel2.text];
        
        number = 0;
        [pointLabel3 setText:[NSString stringWithFormat:@"%d", number]];
        [pointLabel4 setText:[NSString stringWithFormat:@"%d", number]];
        
        
        [self updatePoints];

        AFSucessPopUp *vc = [[AFSucessPopUp alloc]initWithNibName:@"AFSucessPopUp" bundle:nil];
        vc.countNumber = [resultDict objectForKey:@"drink_count"];
        vc.view.frame = CGRectMake(0, 0, 200, 275);
        [self presentPopUpViewController:vc];
        
        
        
        
    }else{
        
        // Show the user an error message
        if ([[responceDict objectForKey:@"message"] isEqualToString:@"user_not_exist"]) {
            [UserPreference alerMassegeWithLogout:responceDict];
        }
        else if ([[responceDict objectForKey:@"message"] isEqualToString:@"user_inactive"]) {
            [UserPreference alerMassegeWithLogout:responceDict];
        }
        else if ([[responceDict objectForKey:@"message"] isEqualToString:@"session_expired"]) {
            [UserPreference alerMassegeWithLogout:responceDict];
        }else{

            NSString *message = [responceDict objectForKey:@"message"];
        [self alerMassege:message];
        }
    }
    
    
}


// Add boll
-(void)initBolls{
    
    
    bollView = [[UIView alloc] initWithFrame:CGRectMake(125, 220+60, MARBLE_WIDTH, MARBLE_HEIGHT)];
    bollView.backgroundColor = [UIColor clearColor];
    
    
    bollView.userInteractionEnabled = YES;
    bollView.layer.cornerRadius = 100/2;
    [self.view addSubview:bollView];
    
    
    UIImageView *boll = [[UIImageView alloc] initWithFrame:CGRectMake(0, 0, MARBLE_WIDTH, MARBLE_HEIGHT)];
    boll.userInteractionEnabled = NO;
    
    boll.image = [UIImage imageNamed:@"Drink-to-swipe"];
    [bollView addSubview:boll];
    
    
    CGAffineTransform transform = CGAffineTransformMakeRotation(M_PI/12);
    [bollView setTransform:transform];
    UISwipeGestureRecognizer *swipeLeftGesture =
    [[UISwipeGestureRecognizer alloc]initWithTarget:self action:@selector(swipeLeft:)];
    swipeLeftGesture.direction = UISwipeGestureRecognizerDirectionLeft;
    swipeLeftGesture.delegate = self;
    [bollView addGestureRecognizer:swipeLeftGesture];
    
    UISwipeGestureRecognizer *swipeUpwardGesture =
    [[UISwipeGestureRecognizer alloc]initWithTarget:self action:@selector(swipeUpward:)];
    swipeUpwardGesture.direction = UISwipeGestureRecognizerDirectionUp;
    swipeUpwardGesture.delegate = self;
    
    [bollView addGestureRecognizer:swipeUpwardGesture];
    
    number = 0;
    [pointLabel3 setText:[NSString stringWithFormat:@"%d", number]];
    [pointLabel4 setText:[NSString stringWithFormat:@"%d", number]];
    
}


// An error occurred, we need to handle the error

-(void)sDrinkWBarHTTPClientAPI:(SDrinkWBarHTTPClientAPI *)client didFailWithError:(NSError *)error{
    
    [SVProgressHUD dismiss];
}



- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end
