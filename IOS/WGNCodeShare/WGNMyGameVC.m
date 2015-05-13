//
//  WGNMyGameVC.m
//  WGN
//
//  Created by Divya Prakash on 19/08/14.
//  Copyright (c) 2014 Kleward Consulting Pvt Ltd. All rights reserved.
//

#import "WGNMyGameVC.h"
#import "WGNMyGameTableViewCell.h"
#import "WGNGameDetailVC.h"

#import "WGNMYGame.h"
#import "ELHeaderView.h"
#import "MyGameHTTPClientAPI.h"

#import "GameDetailsHTTPClientAPI.h"
#import "SVPullToRefresh.h"
#import "DeleteGameHTTPClientAPI.h"

#import "GameStatsHTTPClientAPI.h"
#import "WGNStatsVC.h"

@interface WGNMyGameVC ()<MyGameHTTPClientAPIDelegate,GameDetailsHTTPClientAPIDelegate,DeleteGameHTTPClientAPIDelegate,GameStatsHTTPClientAPIDelegate>{
    NSMutableArray *gamelistArr;
}
@property (nonatomic, weak) ELHeaderView *headerView;

@end

@implementation WGNMyGameVC

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)viewDidLoad
{
    
    NSString *savedValue = [[NSUserDefaults standardUserDefaults]
                            stringForKey:@"Editcheck"];
    if ([savedValue isEqualToString:@"edit"])
    {
        [self loadpicker];
        
    }

    [super viewDidLoad];
    [self initUI];
    
    
    __weak WGNMyGameVC *weakSelf = self;
    
    [self.myGameTableView addPullToRefreshWithActionHandler:^{
        [weakSelf getGameListFromServer];
    }];
    
    
    UIBarButtonItem *statsButton = [[UIBarButtonItem alloc] initWithTitle:@"Stats"style:UIBarButtonItemStylePlain target:self action:@selector(getUserStatsFromServer)];
    self.navigationItem.rightBarButtonItem = statsButton;

    
}

#pragma mark deleteGame


-(void)deleteGameFromServer:(NSString *)gameID{
    
    
    /*
     
     "X_REST_USERNAME"=> "wgnApi",
     "X_REST_PASSWORD"=> "pwgnApi",
     "game_id"=>31
     
     */
    
    NSMutableDictionary *parameters  = [NSMutableDictionary dictionary];
    parameters[@"X_REST_USERNAME"]   = [WGNUserPreference getScreteAPIKey];
    parameters[@"X_REST_PASSWORD"]   = [WGNUserPreference getScreteAPIPassword];
    
    parameters[@"game_id"]           = gameID;
    [SVProgressHUD show];
    DeleteGameHTTPClientAPI * myGameClient = [DeleteGameHTTPClientAPI sharedDeleteGameAPIHTTPClient];
    myGameClient.delegate = self;
    [myGameClient deleteGame:parameters];
    
}


-(void)deleteGameHTTPClientAPI:(DeleteGameHTTPClientAPI *)client didSuccess:(id)response{
    
    
    NSDictionary *responceDict = (NSDictionary *)response;
    NSLog(@"responceDict %@",responceDict);
    
    NSDictionary *delete_response = [responceDict objectForKey:@"DeleteGame_response"];
    
    
    BOOL isSuccess = [[delete_response objectForKey:@"success"]boolValue];
    if (isSuccess) {
        
        NSString *message= [delete_response objectForKey:@"message"];
        [self alerMassege:message];
        
    }else{
        
        NSString *message= [delete_response objectForKey:@"message"];
        [self alerMassege:message];
        
    }
    
    [self.myGameTableView reloadData];
    [SVProgressHUD dismiss];
    
}


-(void)deleteGameHTTPClientAPI:(DeleteGameHTTPClientAPI *)client didFailWithError:(NSError *)error{
    [SVProgressHUD dismiss];
    
}

#pragma mark UITableView Datasource for GameList


-(void)getGameListFromServer {
    
    [SVProgressHUD show];
    MyGameHTTPClientAPI * myGameClient = [MyGameHTTPClientAPI sharedMyGameAPIHTTPClient];
    myGameClient.delegate = self;
    [myGameClient getMyGameListData];
    
}


-(void)myGameHTTPClientAPI:(MyGameHTTPClientAPI *)client didSuccess:(id)response{
    
    
    NSDictionary *responceDict = (NSDictionary *)response;
    NSLog(@"responceDict %@",responceDict);
    
    NSDictionary *myGame_response = [responceDict objectForKey:@"MyGame_response"];
    
    
    BOOL isSuccess = [[myGame_response objectForKey:@"success"]boolValue];
    if (isSuccess) {
        
        gamelistArr = [NSMutableArray new];
        
        NSArray *game_list_array = [myGame_response objectForKey:@"game_list"];
        
        for (int i = 0 ; i<game_list_array.count; i++) {
            
            NSDictionary *gameDict = [game_list_array objectAtIndex:i];
            
            WGNMYGame *game = [WGNMYGame new];
            
            game.gameID        = [gameDict objectForKey:@"game_id"];
            game.gameName      = [gameDict objectForKey:@"game_name"];
            game.gameTime      = [gameDict objectForKey:@"game_time"];
            game.gameDate      = [gameDict objectForKey:@"game_date"];
            game.gameImageURL  = [gameDict objectForKey:@"game_image"];
            
            [gamelistArr addObject:game];
            
            
        }
    }
    
    if (gamelistArr.count == 0 ) {
        NSString *message = [myGame_response objectForKey:@"message"];
        [self.myGameTableView setSeparatorStyle:UITableViewCellSeparatorStyleNone];

        [self showPopup:message];
        
    }else{
        
        self.myGameTableView.backgroundView = nil;
        [self.myGameTableView setSeparatorStyle:UITableViewCellSeparatorStyleSingleLine];
    }
    
    [self.myGameTableView reloadData];
    [SVProgressHUD dismiss];
    [self.myGameTableView.pullToRefreshView stopAnimating];
    
}


-(void)myGameHTTPClientAPI:(MyGameHTTPClientAPI *)client didFailWithError:(NSError *)error{
    [SVProgressHUD dismiss];
    
}



-(void)showPopup:(NSString *)message{
    
    long sections = [self.myGameTableView numberOfSections];
    BOOL hasRows = NO;
    for (int i = 0; i < sections; i++)
        hasRows = ([self.myGameTableView numberOfRowsInSection:i] > 0) ? YES : NO;
    
    if (sections == 0 || hasRows == NO)
    {
        UIImage *image = [UIImage imageNamed:@"white_image"];
        UIImageView *imageView = [[UIImageView alloc] initWithImage:image];
        
        UILabel  * label = [[UILabel alloc] initWithFrame:CGRectMake(10, 100, 300, 50)];
        label.backgroundColor = [UIColor clearColor];
        label.textAlignment = NSTextAlignmentCenter; // UITextAlignmentCenter, UITextAlignmentLeft
        label.textColor=[UIColor grayColor];
        label.numberOfLines=4;
        label.lineBreakMode=NSLineBreakByWordWrapping;
        label.text = message;
        [imageView addSubview:label];
        
        self.myGameTableView.backgroundView = imageView;
        [self.myGameTableView setSeparatorStyle:UITableViewCellSeparatorStyleNone];

    }
    
}

#pragma mark UITableView appearance

-(void)viewWillAppear:(BOOL)animated{
    
    

    self.view.backgroundColor = [WGNComman setRandomBackgroundColor];

    [self getGameListFromServer];
    self.navigationItem.title = @"MY GAMES";
    
  }

-(void)loadpicker
{
    
    UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Alert!" message:@"Please select any Game to create team" delegate:self cancelButtonTitle:@"OK" otherButtonTitles:nil];
    [alert show];

    
}
  
-(IBAction)gameDetails:(id)sender{
    
    
}

#pragma mark UIDesign

-(void)initUI{
    
    [[UINavigationBar appearance] setBarTintColor:[UIColor colorWithRed:14/255.0 green:73/255.0 blue:116/255.0 alpha:1.0]];
    self.navigationController.navigationBar.tintColor = [UIColor whiteColor];
    [self.navigationController.navigationBar setTitleTextAttributes:@{NSForegroundColorAttributeName : [UIColor whiteColor]}];
    self.navigationController.navigationBar.translucent = NO;


    
    
    self.myGameTableView.frame = self.view.bounds;
    self.myGameTableView.delegate = self;
    self.myGameTableView.dataSource = self;
    self.myGameTableView.opaque = NO;
    self.myGameTableView.backgroundColor = [UIColor clearColor];
    [self.myGameTableView registerNib:[UINib nibWithNibName:@"WGNMyGameTableViewCell" bundle:nil] forCellReuseIdentifier:@"myGameTableViewCell"];
    
}

- (BOOL)tableView:(UITableView *)tableView canEditRowAtIndexPath:(NSIndexPath *)indexPath {
    // Return YES if you want the specified item to be editable.
    return YES;
}

// Override to support editing the table view.
- (void)tableView:(UITableView *)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle forRowAtIndexPath:(NSIndexPath *)indexPath {
    if (editingStyle == UITableViewCellEditingStyleDelete) {
        
        WGNMYGame *games = nil;
        games = [gamelistArr objectAtIndex:indexPath.row];
        [self deleteGameFromServer:games.gameID];
        [gamelistArr removeObjectAtIndex:indexPath.row];
        [tableView reloadData];
    }
}


-(void)alerMassegeWithError:(NSString*)message{
    UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"" message:message delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
    [alert show];
    
}

#pragma mark -
#pragma mark UITableView Delegate



- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    WGNMYGame *games = nil;
    games = [gamelistArr objectAtIndex:indexPath.row];
    [self getGameDetailsFromServer:games.gameID];
}

#pragma mark -
#pragma mark UITableView Datasource

- (CGFloat)tableView:(UITableView *)aTableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    [self tableView:self.myGameTableView cellForRowAtIndexPath:indexPath];
    return 76;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)sectionIndex
{
    return [gamelistArr count];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *cellIdentifier = @"myGameTableViewCell";
    
    WGNMyGameTableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:cellIdentifier];
    
    if (cell == nil) {
        cell = [[WGNMyGameTableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:cellIdentifier];
    }
    cell.selectionStyle = UITableViewCellSelectionStyleNone;
    
    WGNMYGame *games = nil;
    
    cell.contentView.backgroundColor = [UIColor clearColor];
    
    games = [gamelistArr objectAtIndex:indexPath.row];
    cell.titleLabel.text = games.gameName;
    cell.timeLabel.text = games.gameTime;
    cell.dateLabel.text = [self dateToFormatedDate:games.gameDate];
    [cell.gamePicture setImageWithURL:[NSURL URLWithString:games.gameImageURL] placeholderImage:[UIImage imageNamed:@"game_thamb_icon"]];
//    [[cell.gamePicture layer] setCornerRadius:29.0f];
    [[cell.gamePicture layer] setMasksToBounds:YES];
    
    
    return cell;
}

- (void) tableView:(UITableView *)tableView willDisplayCell:(UITableViewCell *)cell forRowAtIndexPath:(NSIndexPath *)indexPath {
    [cell setBackgroundColor:[UIColor clearColor]];
}


#pragma mark formateDate


-(NSString *)dateToFormatedDate:(NSString *)dateStr{
    NSString *finalDate = @"2014-10-15";
    NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
    [dateFormatter setDateFormat:@"yyyy-MM-dd"];
    NSDate *date = [dateFormatter dateFromString:dateStr];
    [dateFormatter setDateFormat:@"EE d, MMM, YYYY"];
    return [dateFormatter stringFromDate:date];
}


-(void)getUserStatsFromServer {
    
    [SVProgressHUD show];
    
    NSMutableDictionary *parameters  = [NSMutableDictionary dictionary];
    parameters[@"X_REST_USERNAME"]   =[WGNUserPreference getScreteAPIKey];
    parameters[@"X_REST_PASSWORD"]   = [WGNUserPreference getScreteAPIPassword];
    parameters[@"user_id"]           = [WGNUserPreference getUserID];
    
    GameStatsHTTPClientAPI * myfriendsClient = [GameStatsHTTPClientAPI sharedGameStatsAPIHTTPClient];
    myfriendsClient.delegate = self;
    [myfriendsClient getGameStats:parameters];
    
}


-(void)gameStatsHTTPClientAPI:(GameStatsHTTPClientAPI *)client didSuccess:(id)response{
    
    
    NSDictionary *responceDict = (NSDictionary *)response;
    NSLog(@"responceDict %@",responceDict);
    
    NSDictionary *gameStats_response = [responceDict objectForKey:@"GameStats_response"];
    
    BOOL isSuccess = [[gameStats_response objectForKey:@"success"]boolValue];
    
    if (isSuccess) {
        
        WGNAppDelegate* appDelegate = (WGNAppDelegate*)[[UIApplication sharedApplication] delegate];
        appDelegate.attendessDict = gameStats_response;
        appDelegate.attendessCount = 0;
        
        WGNStatsVC *statsVC = [[WGNStatsVC alloc]initWithNibName:@"WGNStatsVC" bundle:nil];
        statsVC.navigationItem.title = @"My Stats";
        self.navigationItem.title = @"";
        [self.navigationController pushViewController:statsVC animated:YES];
        
        
    }else{
        
        NSString *message = [gameStats_response objectForKey:@"message"];
        [self alerMassege:message];
        
    }
    
    [SVProgressHUD dismiss];
    
}


-(void)gameStatsHTTPClientAPI:(GameStatsHTTPClientAPI *)client didFailWithError:(NSError *)error{
    
    [SVProgressHUD dismiss];
    
}


-(void)getGameDetailsFromServer:(NSString *)gameID {
    
    
    [SVProgressHUD show];
    
    NSMutableDictionary *parameters  = [NSMutableDictionary dictionary];
    parameters[@"X_REST_USERNAME"]   = [WGNUserPreference getScreteAPIKey];
    parameters[@"X_REST_PASSWORD"]   = [WGNUserPreference getScreteAPIPassword];
    parameters[@"user_id"]           = [WGNUserPreference getUserID];
    parameters[@"game_id"]          = gameID;
    
    GameDetailsHTTPClientAPI * myGameClient = [GameDetailsHTTPClientAPI sharedGameDetailsAPIHTTPClient];
    myGameClient.delegate = self;
    [myGameClient getGameDetails:parameters];
    
}


-(void)gameDetailsHTTPClientAPI:(GameDetailsHTTPClientAPI *)client didSuccess:(id)response{
    
    NSDictionary *responceDict = (NSDictionary *)response;
    
    NSDictionary *myGame_response = [responceDict objectForKey:@"GameDetails_response"];
    
    BOOL isSuccess = [[myGame_response objectForKey:@"success"]boolValue];
    
    if (isSuccess) {
        
        NSDictionary *gameDetailsDict = [myGame_response objectForKey:@"gameDetails"];
        WGNGameDetailVC *gameDetailsVC = [[WGNGameDetailVC alloc] initWithNibName:@"WGNGameDetailVC" bundle:nil];
        self.navigationItem.title = @"";
        gameDetailsVC.detailsDict = gameDetailsDict;
        gameDetailsVC.title = @"";
        gameDetailsVC.isME = YES;
        [self.navigationController pushViewController:gameDetailsVC animated:YES];
        
    }
    else{
        
        NSString *message = [myGame_response objectForKey:@"message"];
        [self alerMassegeWithError:message];
        
    }
    
    [SVProgressHUD dismiss];
    
}


-(void)gameDetailsHTTPClientAPI:(GameDetailsHTTPClientAPI *)client didFailWithError:(NSError *)error{
    [SVProgressHUD dismiss];
}

-(void)alerMassege :(NSString *)messageString{
    UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"" message:messageString delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
    [alert show];
    
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
}

@end
