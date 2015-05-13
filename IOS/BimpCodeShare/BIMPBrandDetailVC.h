//
//  BIMPBrandDetailVC.h
//  BIMP
//
//  Created by Anand Prakash on 20/10/14.
//  Copyright (c) 2014 Kleward Consulting Pvt Ltd. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "HMSegmentedControl.h"
#import "WGNBrands.h"

@class GADBannerView;
NSString *favString;
BOOL isFav;

@interface BIMPBrandDetailVC : UIViewController <UIScrollViewDelegate ,UIGestureRecognizerDelegate>



{
    NSString *str;
}
@property(nonatomic) BOOL isFromHome;

@property(nonatomic, weak) IBOutlet UIView *contentView;
@property(nonatomic, weak) IBOutlet UIView *banner;
@property (nonatomic,strong)NSString *brandID;
@property (nonatomic,strong)NSString *brandsID;


@property(nonatomic, strong) WGNBrands *brands;

@end
