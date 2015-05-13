//
//  BIMPBrandDetailVC.m
//  BIMP
//
//  Created by Anand Prakash on 20/03/15.
//  Copyright (c) 2015 Kleward Consulting Pvt Ltd. All rights reserved.
//

#import "BIMPBrandDetailVC.h"
#import "BIMPTableVC.h"
#import "BIMPBrandVC.h"
#import "BIMPBrandLocationVC.h"
#import "ReadStatusHTTPClientAPI.h"
#import "BIMPFavoriteVC.h"
#import "UIViewController+CWPopup.h"
#import "SamplePopupViewController.h"

#import "GADBannerView.h"
#import "GADRequest.h"
#import "LikeBrandsHTTPClientAPI.h"
#define GAD_SIMULATOR_ID @"Simulator"


@interface BIMPBrandDetailVC ()<ReadStatusHTTPClientAPIDelegate,LikeBrandsHTTPClientAPIDelegate>
@property (nonatomic, strong) UIScrollView *scrollView;
@property (nonatomic, strong) HMSegmentedControl *segmentedControl4;


@property (nonatomic, strong) IBOutlet UIView *placeholderView;
@property (nonatomic, strong) IBOutlet UISegmentedControl *segControl;


@property (nonatomic, strong) BIMPTableVC *child1;
@property (nonatomic, strong) BIMPBrandVC *child3;
@property (nonatomic, strong) BIMPBrandLocationVC *child2;
@property (nonatomic, strong) SamplePopupViewController *child4;


@end

@implementation BIMPBrandDetailVC

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}
- (void)viewWillAppear:(BOOL)animated{
    
    self.title = self.brands.brandName;
   // [self.segControl insertSegmentWithTitle:@"" atIndex:3 animated:YES];


}

- (void)viewDidLoad
{
    NSString *savedValue = [[NSUserDefaults standardUserDefaults]
                            stringForKey:@"UpdateSegment"];
    if ([savedValue isEqualToString:@"Cellview"])
    {
        
    [self.segControl removeSegmentAtIndex:3 animated:YES];
    }
//    else
//    {
//        [self.segControl insertSegmentWithTitle:@"feb" atIndex:3 animated:YES];
//        
//    }
    
    NSLog(@"brands %@",self.brands.brandNotificationDetailsDict);
    NSString *brandID = self.brands.brandID;
    NSLog(@"brands %@",self.brands.brandID);
    [self updateBrandsReadStatusOnServer:brandID];

    
    GADBannerView *bannerView_ = [[GADBannerView alloc] initWithAdSize:kGADAdSizeBanner];
    bannerView_.adUnitID = GOOGLE_ADS_ID;
    bannerView_.rootViewController = self;
    [bannerView_ loadRequest:[GADRequest request]];
    [self.banner addSubview:bannerView_];

    [super viewDidLoad];
    

    self.navigationController.navigationBar.translucent = NO;
    
    
    self.segControl.tintColor =  UIColorFromRGB(0xF25829);
    // The attributes dictionary can specify the font, text color, text shadow color, and text
    // shadow offset for the title in the text attributes dictionary
    [self.segControl setTitleTextAttributes:@{NSForegroundColorAttributeName:[UIColor grayColor]} forState:UIControlStateNormal];
    
    
    
    self.child1 = [[BIMPTableVC alloc]initWithNibName:@"BIMPTableVC" bundle:nil];
    self.child1.array =  (NSArray *)self.brands.brandNotificationDetailsDict;
    
    self.child3 = [[BIMPBrandVC alloc]initWithNibName:@"BIMPBrandVC" bundle:nil];
   self.child3.brands =  self.brands;

    self.child2 = [[BIMPBrandLocationVC alloc]initWithNibName:@"BIMPBrandLocationVC" bundle:nil];
    self.child2.locationDataSource =  (NSArray *)self.brands.brandLocationDetailsDict;
    self.child2.brandsName =  self.brands.brandName;
    
    self.child4 = [[SamplePopupViewController alloc]initWithNibName:@"SamplePopupViewController" bundle:nil];
    
    
    [self addChildViewController:self.child1];
    [self.child1 didMoveToParentViewController:self];
    
    [self addChildViewController:self.child3];
    [self.child3 didMoveToParentViewController:self];
    
    [self addChildViewController:self.child4];
    [self.child4 didMoveToParentViewController:self];
    
//  self.child2 = [self.storyboard instantiateViewControllerWithIdentifier:@"child2"];
    [self addChildViewController:self.child2];
    [self.child2 didMoveToParentViewController:self];

    
    // by defoult child 1 is visible
    
    [self addChild:self.child1 withChildToRemove:nil];

    
self.view.backgroundColor = [UIColor whiteColor];
    
    CGFloat yDelta;
    
    if ([[[UIDevice currentDevice] systemVersion] compare:@"7.0" options:NSNumericSearch] != NSOrderedAscending) {
        yDelta = 20.0f;
    } else {
        yDelta = 0.0f;
    }
    

    
    UIButton *shareBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    shareBtn.userInteractionEnabled = YES;
    shareBtn.frame = CGRectMake(0, 0, 30 , 30);
    shareBtn.backgroundColor = [UIColor clearColor];
 //   [shareBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal ];
    
    
    UIImage *buttonImageNormal = [UIImage imageNamed:@"selected_heart.png"];
    UIImage *strechableButtonImageNormal = [buttonImageNormal stretchableImageWithLeftCapWidth:12 topCapHeight:0];
    [shareBtn setBackgroundImage:strechableButtonImageNormal forState:UIControlStateNormal];

    
    UIImage *buttonImagePressed = [UIImage imageNamed:@"un_selected_red_heart.png"];
    UIImage *strechableButtonImagePressed = [buttonImagePressed stretchableImageWithLeftCapWidth:12 topCapHeight:0];
    [shareBtn setBackgroundImage:strechableButtonImagePressed forState:UIControlStateSelected];
    
    
 //   NSLog(@"self.brands.brandIsLike %@",self.brands.brandIsLike);
    
  //  BOOL isLike = [self.brands.brandIsLike boolValue];
    
    if ([self.brands.brandIsLike isEqualToString:@"1"]) {
        [shareBtn setSelected:YES];
    }else{
        [shareBtn setSelected:NO];
    }
    
    
    // favourite button
    
    [shareBtn addTarget:self action:@selector(likeBrandsDialoga:) forControlEvents:UIControlEventTouchUpInside];

    
    [self.navigationItem setRightBarButtonItem:[[UIBarButtonItem alloc] initWithCustomView:shareBtn]];

    UITapGestureRecognizer *tapRecognizer = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(dismissPopup)];
    tapRecognizer.numberOfTapsRequired = 1;
    tapRecognizer.delegate = self;
    
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(dismissPopup)
                                                 name:@"dissmissPop"
                                               object:nil];


}


# pragma mark Make Favourite Brand

- (IBAction)likeBrandsDialoga:(UIButton *)sender {

    UIButton *button = (UIButton *)sender;
    button.selected = ![button isSelected];

    if ([button isSelected] == YES) {
       
        [sender setBackgroundImage:[UIImage imageNamed:@"un_selected_red_heart.png"] forState:UIControlStateSelected];
        
    
        BIMPAppDelegate *appDelegate = (BIMPAppDelegate*)[[UIApplication sharedApplication] delegate];
        appDelegate.favStringDelegate = self.brands.notificationTypeId ;

      //  anand hide popup
//        [self presentPopupViewController:samplePopupViewController animated:YES completion:^(void) {
//         NSLog(@"popup view presented");
//        }];
        
        //-----------------------------------
        
        favString = @"";
            favString = [NSString stringWithFormat:@"%@%@",favString,@"1"];
            favString = [NSString stringWithFormat:@"%@%@",favString,@",2"];
            favString = [NSString stringWithFormat:@"%@%@",favString,@",3"];
            favString = [NSString stringWithFormat:@"%@%@",favString,@",4"];
        
        
        NSLog(@"favString %@ ",favString);
        if (button.isSelected == YES) {
            
            isFav = YES;
            str =@"1";

        }else{
            
            isFav = NO;
            str =@"0";
            
        }
        
        
        //[SVProgressHUD show];
        
        
        NSString *brndID = [[NSUserDefaults standardUserDefaults]
                                stringForKey:@"brandsid"];
        
        NSLog(@"brandsid %@",brndID);
        
       
        
        NSMutableDictionary *parameters  = [NSMutableDictionary dictionary];
        parameters[@"X_REST_USERNAME"]   = [BIMPUserPreference getScreteAPIKey];
        parameters[@"X_REST_PASSWORD"]   = [BIMPUserPreference getScreteAPIPassword];
        parameters[@"user_id"]           =   [BIMPUserPreference getUserID];
        NSLog(@"brandID %@",brndID);
        parameters[@"favorite_brand_id"] =  brndID;
        parameters[@"is_fav"]= str;
        parameters[@"update_type"]= str;
        parameters[@"notification_type_ids"]=  favString;
        NSLog(@"parameters %@",parameters);
        LikeBrandsHTTPClientAPI * readStatus = [LikeBrandsHTTPClientAPI sharedLikeBrandsAPIHTTPClient];
        readStatus.delegate = self;
        [readStatus changeBrandsLikeStatus:parameters] ;
        
        
        
        
        //-----------------------------------
        
    }else{
                         // isSelected = NO
                        //   [[NSNotificationCenter defaultCenter] postNotificationName:@"reloadFevBrandsList" object:self];
        [sender setBackgroundImage:[UIImage imageNamed:@"selected_heart.png"] forState:UIControlStateNormal];
        [self unlikeFevBrands];
    }
    
    UIImage *image = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();
    button.backgroundColor = [UIColor colorWithPatternImage:image];

    
    
}

#pragma mark dismiss Popup

- (void)dismissPopup {
    
    if (self.popupViewController != nil) {
        [self dismissPopupViewControllerAnimated:YES completion:^{
            NSLog(@"popup view dismissed");
        }];
    }
}



#pragma mark getbrandStatus

-(void)updateBrandsReadStatusOnServer :(NSString *)brandID {
    //[SVProgressHUD show];
    
    NSMutableDictionary *parameters  = [NSMutableDictionary dictionary];
    parameters[@"X_REST_USERNAME"]   = [BIMPUserPreference getScreteAPIKey];
    parameters[@"X_REST_PASSWORD"]   = [BIMPUserPreference getScreteAPIPassword];
    parameters[@"user_id"]           = [BIMPUserPreference getUserID];
    parameters[@"brand_id"]          = brandID;
    
  //  NSString *brandist=[brand objectForKey:@"brand_id"];
    
    [[NSUserDefaults standardUserDefaults] setObject:brandID forKey:@"brandsid"];
    [[NSUserDefaults standardUserDefaults]synchronize];

    
    ReadStatusHTTPClientAPI * readStatus = [ReadStatusHTTPClientAPI sharedReadStatusAPIHTTPClient];
    readStatus.delegate = self;
    [readStatus updateReadStatus:parameters] ;
    
}


-(void)readStatusHTTPClientAPI:(ReadStatusHTTPClientAPI *)client didSuccess:(id)response{
    NSDictionary *responceDictt = (NSDictionary *)response;
    NSDictionary *responceDict = [responceDictt objectForKey:@"UpdateBrandStatus_response"];

    BOOL isSuccess = [[responceDict objectForKey:@"success"]boolValue];
    if ( isSuccess== YES) {
        
        
        if (self.isFromHome==YES) {
            [[NSNotificationCenter defaultCenter] postNotificationName:@"reloadBrandsList" object:self];
        }else{
           // [[NSNotificationCenter defaultCenter] postNotificationName:@"reloadFevBrandsList" object:self];

        }
    }

}


-(void)readStatusHTTPClientAPI:(ReadStatusHTTPClientAPI *)client didFailWithError:(NSError *)error{
    
    //[SVProgressHUD dismiss];
    
}


# pragma Mark make unfavourite brand

-(void)unlikeFevBrands{
    
    [[NSNotificationCenter defaultCenter] postNotificationName:@"dissmissPop" object:self];
    
    
    BIMPAppDelegate *appDelegate = (BIMPAppDelegate*)[[UIApplication sharedApplication] delegate];
    NSString *favString = appDelegate.favStringDelegate ;
    
    
   //[SVProgressHUD dismiss];
    
    
    NSString *brandID = self.brands.brandID ;
    
    NSMutableDictionary *parameters  = [NSMutableDictionary dictionary];
    parameters[@"X_REST_USERNAME"]   = [BIMPUserPreference getScreteAPIKey];
    parameters[@"X_REST_PASSWORD"]   = [BIMPUserPreference getScreteAPIPassword];
    parameters[@"user_id"]           =   [BIMPUserPreference getUserID];
    parameters[@"favorite_brand_id"] =  brandID;
    
    parameters[@"update_type"]       = @"0";
    parameters[@"notification_type_ids"]=  favString;

    
    
    NSLog(@"parameters %@",parameters);
    
   

    
    LikeBrandsHTTPClientAPI * readStatus = [LikeBrandsHTTPClientAPI sharedLikeBrandsAPIHTTPClient];
    readStatus.delegate = self;
    [readStatus changeBrandsLikeStatus:parameters] ;
    
    
    
}



-(void)likeBrandsHTTPClientAPI:(LikeBrandsHTTPClientAPI *)client didSuccess:(id)response{
    
    
    NSDictionary *responceDictt = (NSDictionary *)response;
    NSLog(@"responceDict %@",responceDictt);
    NSDictionary *responceDict = [responceDictt objectForKey:@"UserUpdateFavorite_response"];
    
    
    BOOL isSuccess = [[responceDict objectForKey:@"success"]boolValue];

    
    if (isSuccess) {
        
        if (self.isFromHome==YES) {
            [[NSNotificationCenter defaultCenter] postNotificationName:@"reloadBrandsList" object:self];
        }else{
            [[NSNotificationCenter defaultCenter] postNotificationName:@"reloadFevBrandsList" object:self];
            
        }
        
    }else{
        
    }
    
    [[NSNotificationCenter defaultCenter] postNotificationName:@"dissmissPop" object:self];
    
//    [SVProgressHUD dismiss];
    
}


-(void)likeBrandsHTTPClientAPI:(LikeBrandsHTTPClientAPI *)client didFailWithError:(NSError *)error{
    
    [SVProgressHUD dismiss];
    
}
- (void)addChild:(UIViewController *)childToAdd withChildToRemove:(UIViewController *)childToRemove
{
    
    self.title = self.brands.brandName;

    assert(childToAdd != nil);
    
    if (childToRemove != nil)
    {
        [childToRemove.view removeFromSuperview];
    }
    
    // match the child size to its parent
    CGRect frame = childToAdd.view.frame;
    frame.size.height = CGRectGetHeight(self.placeholderView.frame);
    frame.size.width = CGRectGetWidth(self.placeholderView.frame);
    childToAdd.view.frame = frame;
    
[self.placeholderView addSubview:childToAdd.view];
}

// user tapped on the segmented control to choose which child is to be visible
- (IBAction)segmentControlAction:(id)sender
{
    UISegmentedControl *segControl = (UISegmentedControl *)sender;
    self.title = self.brands.brandName;
    UIViewController *childToAdd, *childToRemove;
    if (segControl.selectedSegmentIndex == 0) {
        childToAdd = self.child1;
    }
    if (segControl.selectedSegmentIndex == 1) {
        childToAdd = self.child2;
    }
    if (segControl.selectedSegmentIndex == 2) {
        childToAdd = self.child3;
    }
    if (segControl.selectedSegmentIndex == 3) {
        childToAdd = self.child4;
    }
//
    [self addChild:childToAdd withChildToRemove:childToRemove];
}


#pragma mark - UIStateRestoration

// encodeRestorableStateWithCoder is called when the app is suspended to the background
- (void)encodeRestorableStateWithCoder:(NSCoder *)coder
{
    NSLog(@"ParentViewController: encodeRestorableStateWithCoder");
    
    // remember our children view controllers
    [coder encodeObject:self.child1 forKey:@"child1"];
    [coder encodeObject:self.child2 forKey:@"child2"];
    [coder encodeObject:self.child3 forKey:@"child3"];
    [coder encodeObject:self.child4 forKey:@"child4"];


    // remember the segmented control state
    [coder encodeInteger:self.segControl.selectedSegmentIndex forKey:@"selectedIndex"];
    
    [super encodeRestorableStateWithCoder:coder];
}

// decodeRestorableStateWithCoder is called when the app is re-launched
- (void)decodeRestorableStateWithCoder:(NSCoder *)coder
{
    NSLog(@"ParentViewController: decodeRestorableStateWithCoder");
    
    // find out which child was the current visible view controller
    self.segControl.selectedSegmentIndex = [coder decodeIntegerForKey:@"selectedIndex"];
    
    // call our segmented control to set the right visible child
    // (note that we already previously have already loaded both children view controllers in viewDidLoad)
    //
    [self segmentControlAction:self.segControl];
    
    [super decodeRestorableStateWithCoder:coder];
}





- (void)setApperanceForLabel:(UILabel *)label {
    CGFloat hue = ( arc4random() % 256 / 256.0 );  //  0.0 to 1.0
    CGFloat saturation = ( arc4random() % 128 / 256.0 ) + 0.5;  //  0.5 to 1.0, away from white
    CGFloat brightness = ( arc4random() % 128 / 256.0 ) + 0.5;  //  0.5 to 1.0, away from black
    UIColor *color = [UIColor colorWithHue:hue saturation:saturation brightness:brightness alpha:1];
    label.backgroundColor = color;
    label.textColor = [UIColor whiteColor];
    label.font = [UIFont systemFontOfSize:21.0f];
    label.textAlignment = NSTextAlignmentCenter;
}


- (void)segmentedControlChangedValue:(HMSegmentedControl *)segmentedControl {
	NSLog(@"Selected index %ld (via UIControlEventValueChanged)", (long)segmentedControl.selectedSegmentIndex);
}

- (void)uisegmentedControlChangedValue:(UISegmentedControl *)segmentedControl {
	NSLog(@"Selected index %ld", (long)segmentedControl.selectedSegmentIndex);
}

#pragma mark - UIScrollViewDelegate

- (void)scrollViewDidEndDecelerating:(UIScrollView *)scrollView {
    CGFloat pageWidth = scrollView.frame.size.width;
    NSInteger page = scrollView.contentOffset.x / pageWidth;
    
    [self.segmentedControl4 setSelectedSegmentIndex:page animated:YES];
}







- (IBAction)likeBrandsDialog:(id)sender{
    
    
    UIButton *button = (UIButton *)sender;
    button.selected = ![button isSelected];
    
    BOOL isSelected;
    
    UIGraphicsBeginImageContext(button.frame.size);
    
    
    

    
}

#pragma mark Sharing link
//------------------Sharing a link using the share dialog------------------
- (IBAction)shareLinkWithShareDialog:(id)sender
{
    
    // Check if the Facebook app is installed and we can present the share dialog
    FBLinkShareParams *params = [[FBLinkShareParams alloc] init];
    params.link = [NSURL URLWithString:@"http://www.gottabemobile.com/2014/10/20/ios-8-1-on-ipad-air-review-early/"];
    
    // If the Facebook app is installed and we can present the share dialog
    if ([FBDialogs canPresentShareDialogWithParams:params]) {
        
        // Present share dialog
        [FBDialogs presentShareDialogWithLink:params.link
                                      handler:^(FBAppCall *call, NSDictionary *results, NSError *error) {
                                          if(error) {
                                              // An error occurred, we need to handle the error
                                              // See: https://developers.facebook.com/docs/ios/errors
                                              NSLog(@"Error publishing story: %@", error.description);
                                          } else {
                                              // Success
                                              NSLog(@"result %@", results);
                                          }
                                      }];
        
        // If the Facebook app is NOT installed and we can't present the share dialog
    } else {
        // FALLBACK: publish just a link using the Feed dialog
        
        // Put together the dialog parameters
        NSMutableDictionary *params = [NSMutableDictionary dictionaryWithObjectsAndKeys:
                                       @"iOS 8.1 on iPad Air", @"name",
                                       @" Impressions & Performance", @"caption",
                                       @"AToday, as promised, Apple has released the iOS 8.1 update for iPhone, iPad and iPod touch.", @"description",
                                       @"http://www.gottabemobile.com/2014/10/20/ios-8-1-on-ipad-air-review-early/", @"link",
                                       @"http://cdn.gottabemobile.com/wp-content/uploads/2014/04/iPad-Air-Review-20-620x413.jpg", @"picture",
                                       nil];
        
        // Show the feed dialog
        [FBWebDialogs presentFeedDialogModallyWithSession:nil
                                               parameters:params
                                                  handler:^(FBWebDialogResult result, NSURL *resultURL, NSError *error) {
                                                      if (error) {
                                                          // An error occurred, we need to handle the error
                                                          // See: https://developers.facebook.com/docs/ios/errors
                                                          NSLog(@"Error publishing story: %@", error.description);
                                                      } else {
                                                          if (result == FBWebDialogResultDialogNotCompleted) {
                                                              // User canceled.
                                                              NSLog(@"User cancelled.");
                                                          } else {
                                                              // Handle the publish feed callback
                                                              NSDictionary *urlParams = [self parseURLParams:[resultURL query]];
                                                              
                                                              if (![urlParams valueForKey:@"post_id"]) {
                                                                  // User canceled.
                                                                  NSLog(@"User cancelled.");
                                                                  
                                                              } else {
                                                                  // User clicked the Share button
                                                                  NSString *result = [NSString stringWithFormat: @"Posted story, id: %@", [urlParams valueForKey:@"post_id"]];
                                                                  NSLog(@"result %@", result);
                                                              }
                                                          }
                                                      }
                                                  }];
    }
}


// A function for parsing URL parameters returned by the Feed Dialog.
- (NSDictionary*)parseURLParams:(NSString *)query {
    NSArray *pairs = [query componentsSeparatedByString:@"&"];
    NSMutableDictionary *params = [[NSMutableDictionary alloc] init];
    for (NSString *pair in pairs) {
        NSArray *kv = [pair componentsSeparatedByString:@"="];
        NSString *val =
        [kv[1] stringByReplacingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
        params[kv[0]] = val;
    }
    return params;
}


@end
