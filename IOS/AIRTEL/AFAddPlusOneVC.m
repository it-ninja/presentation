//
//  AFAddPlusOneVC.m
//  Unliminet
//
//  Created by Apple on 26/03/15.
//  Copyright (c) 2015 Kleward. All rights reserved.
//

#import "AFAddPlusOneVC.h"
#import "UIViewController+CWPopup.h"
#include "AFInvitePopUp.h"
#import "UIViewController+ENPopUp.h"
#import "AFPlueOneList.h"
#import "TicketInfoHTTPClientAPI.h"
#import "AFPlusOnePop.h"

#import "AFAlerPopupVC.h"

@interface AFAddPlusOneVC ()<UIGestureRecognizerDelegate,TicketInfoHTTPClientAPIDelegate>{
    
    IBOutlet UIButton *homeButton;
    IBOutlet UIImageView *ticketButton;
    IBOutlet UIImageView *ticketImageView;
    
    
    IBOutlet UILabel *footerTitle;
    
    
    NSDictionary *ticketDict;
    
    BOOL isSwipeTicket;
    BOOL isSwipeFirst;;
    IBOutlet UILabel *partyNameLabel;

    
}


@end

@implementation AFAddPlusOneVC

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}


-(void) viewWillAppear:(BOOL)animated {
    
    [super viewWillAppear:animated];
    self.screenName = @"AddPlusOne Screen";
}


- (void)viewDidLoad
{
    [super viewDidLoad];
    
    partyNameLabel.text = [UserPreference getPartyName];

    [homeButton addTarget:(AFONavigationController *)self.navigationController action:@selector(showMenu) forControlEvents:UIControlEventTouchUpInside];
    
    // Add Swipe Gesture
    
    UISwipeGestureRecognizer *swipeUPGesture =
    [[UISwipeGestureRecognizer alloc]initWithTarget:self action:@selector(swipeUP:)];
    swipeUPGesture.direction = UISwipeGestureRecognizerDirectionUp;
    swipeUPGesture.delegate = self;
    [ticketButton addGestureRecognizer:swipeUPGesture];
    ticketButton.hidden = YES;
    
    // Add Notification Center
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(dismissPopup:)
                                                 name:@"dismissPopup"
                                               object:nil];
    
    
    
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(dismissPopupSwipeVC:)
                                                 name:@"dismissPopupSwipeVC"
                                               object:nil];
    
    
    // get Ticket Information From Server

    [self getTicketInfoFromServer];
    
    isSwipeTicket  = YES;
    ticketButton.hidden  = YES;
    footerTitle.hidden = YES;
    
    
    
    // Check Ticket Title for give Away and add plus one
    if (self.giveAwayTicket==YES) {
        
        footerTitle.text = @"Swipe upwards to give away your ticket. If you do this, your ticket is gone for good!";
        
    }else{
        
        footerTitle.text = @"Swipe upwards to invite your plus-one to the party.";
        
    }
    
    
    
}

// Add Custom popup
-(void)swipeFirstAlerMassege:(NSDictionary *)dict{
    
    AFPlusOnePop *vc = [[AFPlusOnePop alloc]initWithNibName:@"AFPlusOnePop" bundle:nil];
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
        isSwipeFirst = YES;
    }
    
    if (self.popupViewController != nil) {
        
        [self dismissPopupViewControllerAnimated:YES completion:^{
            NSLog(@"popup view dismissed");
            
            [self performSelector:@selector(swipeUP:) withObject:self afterDelay:0.5 ];
            
            
        }];
    }
}







- (void)swipeUP:(UISwipeGestureRecognizer *)sender
{
    
    
    
    [UIView animateWithDuration:1.5
                          delay:0
                        options: UIViewAnimationOptionCurveEaseInOut
                     animations:^{
                         ticketButton.frame = CGRectMake(ticketButton.frame.origin.x, 90, 240, 175);
                     }
                     completion:^(BOOL finished){
                         NSLog(@"Done!");
                         
                         ticketButton.hidden = YES;
                         
                         
                         
                         if (self.giveAwayTicket==YES) {
                             [self openGiveAwayListVC:sender];
                             
                         }else{
                             [self openAddOneListVC:sender];
                         }
                         
                         
                     }];
    
    
    
}


// Opne PlueOne friends list

-(void)openAddOneListVC:(id)sender{
    AFPlueOneList *plueOneList = [[AFPlueOneList alloc] initWithNibName:@"AFPlueOneList" bundle:nil];
    plueOneList.ticketShareDict = self.ticketInfoDict;
    [self presentPopupViewController:plueOneList animated:YES completion:^(void) {
        NSLog(@"popup view presented");
    }];
}


// Opne GiveAway friends list

-(void)openGiveAwayListVC:(id)sender{
    AFBarVC *plueOneList = [[AFBarVC alloc] initWithNibName:@"AFBarVC" bundle:nil];
    plueOneList.ticketShareDict = self.ticketInfoDict;
    [self presentPopupViewController:plueOneList animated:YES completion:^(void) {
        NSLog(@"popup view presented");
    }];
}



#pragma mark - Popup Functions
- (void)dismissPopup:(id)sender {
    if (self.popupViewController != nil) {
        [self dismissPopupViewControllerAnimated:YES completion:^{
            NSLog(@"popup view dismissed");
            
            ticketButton.hidden = NO;
            ticketButton.frame = CGRectMake(39, 273, 240, 175);
            
            
            
        }];
    }
}




- (BOOL)gestureRecognizer:(UIGestureRecognizer *)gestureRecognizer shouldRecognizeSimultaneouslyWithGestureRecognizer:(UIGestureRecognizer *)otherGestureRecognizer
{
    return YES;
}





/* *************************< Get Ticket Info From Server >*************************** */




-(void)getTicketInfoFromServer {
    
    /*
     X_REST_USERNAME	airtelSocialParty	Application user name
     X_REST_PASSWORD	pAirtelSocialParty	Application password.
     user_id*	1	User id provided at the time of login.
     party_key*	unliminet	Provided at the time of login
     */
    
    [SVProgressHUD show];
    
    NSMutableDictionary *parameters = [NSMutableDictionary dictionary];
    
    parameters[@"X_REST_USERNAME"]      = [UserPreference getScreteAPIKey];
    parameters[@"X_REST_PASSWORD"]      = [UserPreference getScreteAPIPassword];
    parameters[@"user_id"]              = [UserPreference getUserID];
    parameters[@"party_key"]            = [UserPreference getPartyKey];
    parameters[@"session_token"]            = [UserPreference getSessionToken];

    
    NSLog(@"parameters %@",parameters);
    
    TicketInfoHTTPClientAPI * ticketInfoHTTPClientAPI = [TicketInfoHTTPClientAPI sharedTicketInfoAPIHTTPClient];
    ticketInfoHTTPClientAPI.delegate = self;
    [ticketInfoHTTPClientAPI getTicketInfo:parameters] ;
    
}


-(void)ticketInfoHTTPClientAPI:(TicketInfoHTTPClientAPI *)client didSuccess:(id)response{
    
    [SVProgressHUD dismiss];
    
    NSDictionary *responceDict = (NSDictionary *)response;
    NSLog(@"responceDict %@",responceDict);
    
    
    /*
     
     result =     {
     "can_give_away" = yes;
     "can_plus_one" = yes;
     "give_away_button_text" = "Give away your ticket";
     "give_away_confirm_box_message" = "Easy now... You're about to give away your invite! Do you wish to continue? ";
     "plus_one_button_text" = "Get your plus-one";
     "plus_one_confirm_box_message" = "Nice!! You have just invited your plus one to the party.";
     "swipe_give_away_text1" = "Swipe upwards to give away your ticket.";
     "swipe_give_away_text2" = "If you do this,your ticket is gone for good!";
     "swipe_plus_one_text" = "Swipe upwards to invite your plus-one to the party";
     "ticket_image" = "http://airtelparty.ongoingprojects.in/images/partyTicketImage/card1@3x_150327054305.png";
     "ticket_text_line1" = "This is your special ticket to the party.";
     "ticket_text_line2" = "Check notifications everytime you need to view it.";
     "ticket_text_line3" = "show this at the venue to gain entry.";
     };
     status = true;
     }
     
     */
    
    BOOL isSuccess = [[responceDict objectForKey:@"status"]boolValue];
    
    if (isSuccess==YES) {
        
        NSDictionary *resultDict = [responceDict objectForKey:@"result"];
        ticketDict = resultDict;
        isSwipeTicket  = YES;
        footerTitle.hidden = NO;
        
        
        NSString *message = [responceDict objectForKey:@"message"];
        
        if ([message isEqualToString:@"You don't have the ticket for this party."]) {
            ticketButton.hidden = YES;
        }else{
            ticketButton.hidden = NO;
            [ticketButton setImageWithURL:[NSURL URLWithString:[responceDict objectForKey:@"ticket_image"]] placeholderImage:nil];
            
        }
        
        
        
    }else{
        
        if ([[responceDict objectForKey:@"message"] isEqualToString:@"user_not_exist"]) {
            [UserPreference alerMassegeWithLogout:responceDict];
        }
        else if ([[responceDict objectForKey:@"message"] isEqualToString:@"user_inactive"]) {
            [UserPreference alerMassegeWithLogout:responceDict];
        }
        else if ([[responceDict objectForKey:@"message"] isEqualToString:@"session_expired"]) {
            [UserPreference alerMassegeWithLogout:responceDict];
        }else{

        
        isSwipeTicket  = NO;
        ticketButton.hidden = YES;
        footerTitle.hidden = YES;
        
        NSString *message = [responceDict objectForKey:@"message"];
        [self alerMassege:message];
        
        }
        
    }
    
    
}



-(void)ticketInfoHTTPClientAPI:(TicketInfoHTTPClientAPI *)client didFailWithError:(NSError *)error{
    
    [SVProgressHUD dismiss];
}




-(void)alerMassege:(NSString *)massege{
    
    AFAlerPopupVC *vc = [[AFAlerPopupVC alloc]initWithNibName:@"AFAlerPopupVC" bundle:nil];
    vc.alertStringTitle = massege;
    vc.view.frame = CGRectMake(0, 0, 200, 245);
    [self presentPopUpViewController:vc];
    
}






- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end
