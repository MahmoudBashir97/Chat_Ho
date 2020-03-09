'use strict'


const functions = require('firebase-functions');
const admin =require('firebase-admin');
admin.initializeApp(functions.config().firebase);

exports.sendNotificationMessage= functions.database.ref('/Notif/{messageRecieverID}/{notification_id}')
.onWrite((data,context) =>{
   
   const messageRecieverID=context.params.messageRecieverID;
   const notification_id=context.params.notification_id;
   
   console.log('we have a notification to send to :', messageRecieverID);
   
   if (!data.after.val())
   {
    console.log('A notification has been deleted :', notification_id);
    return null;
   }
   
   
   const sender_user_id=admin.database().ref('/Notif/${messageRecieverID}/&{notification_id}').once('value');
   
   return sender_user_id.then(fromUserResult =>{
          const from_sender_user_id =fromUserResult.val().from;
          console.log('you have a notification from:',sender_user_id);
		  
          const userQuery =admin.database().ref('/Users/${messageRecieverID}/device_Tokens').once('value');
          return userQuery.then(userResult =>{
                 const senderUserName = userResult.val();
				 const message=userResult.val();
            });
        
         const deviceToken=admin.database().ref('/Users/${messageRecieverID}/device_Tokens').once('value');
   return deviceToken.then(result =>
                           {
                            const token_id=result.val();
                            const payload=
                            {
                                notification:
                                {
                                    from_sender_user_id:from_sender_user_id,
                                    title:'New Message From ${senderUserName}',
                                    body :'${message}.',
                                    icon :"default"
                                }
                            };
                            return admin.messaging().sendToDevice(token_id,payload)
                                   .then(response =>{
                                    console.log('this was a notification feature.');
                                    });
                                   
                                   
                            });
    });
   
    
});

