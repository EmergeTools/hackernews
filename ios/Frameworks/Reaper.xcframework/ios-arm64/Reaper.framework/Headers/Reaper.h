#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface EMGReaper : NSObject

+ (instancetype)sharedInstance;
- (void)startWithAPIKey:(NSString *)APIKey;

- (instancetype)init NS_UNAVAILABLE;
+ (instancetype)new NS_UNAVAILABLE;

@end

NS_ASSUME_NONNULL_END
