//
//  WGNMyGameVC.h
//  WGN
//
//  Created by Anand Prakash on 19/04/15.
//  Copyright (c) 2015 Kleward Consulting Pvt Ltd. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface WGNMyGameVC : UIViewController<UITableViewDataSource,UITableViewDelegate>
@property(nonatomic,strong)IBOutlet UITableView *myGameTableView;

@end
