import 'package:flutter/material.dart';
import 'package:mobile_map_core_example/LandingPage.dart';
import 'package:mobile_map_core_example/SessionManager.dart';
import 'package:mobile_map_core_example/repository.dart';

/// description:
/// project: mobile_map_core
/// @package: example.lib
/// @author: dammyololade
/// created on: 29/07/2020

class LoginScreen extends StatefulWidget {
  @override
  _LoginScreenState createState() => _LoginScreenState();
}

class _LoginScreenState extends State<LoginScreen> {
  var _emailController = TextEditingController();
  var _passwordController = TextEditingController();
  String selectedType = "Customer";
  bool isLoading = false;
  var _scaffoldKey = GlobalKey<ScaffoldState>();

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      key: _scaffoldKey,
      appBar: AppBar(
        title: Text("Login"),
      ),
      body: SingleChildScrollView(
        child: Container(
          padding: EdgeInsets.symmetric(vertical: 30, horizontal: 20),
          child: Column(
            children: [
              SizedBox(
                height: 100,
              ),
              Container(
                padding: EdgeInsets.symmetric(horizontal: 10, vertical: 5),
                decoration: BoxDecoration(
                    borderRadius: BorderRadius.all(Radius.circular(5)),
                    border: Border.all(color: Colors.grey)),
                child: DropdownButton(
                    items: ["Customer", "Partner", "Admin"]
                        .map((e) =>
                            DropdownMenuItem<String>(value: e, child: Text(e)))
                        .toList(),
                    value: selectedType,
                    isDense: false,
                    isExpanded: true,
                    onChanged: (String value) {
                      setState(() {
                        selectedType = value;
                      });
                    }),
              ),
              SizedBox(
                height: 20,
              ),
              TextFormField(
                controller: _emailController,
                decoration: InputDecoration(
                    labelText: "Email", border: OutlineInputBorder()),
              ),
              SizedBox(
                height: 20,
              ),
              TextFormField(
                controller: _passwordController,
                obscureText: true,
                decoration: InputDecoration(
                    labelText: "Password", border: OutlineInputBorder()),
              ),
              SizedBox(
                height: 50,
              ),
              (!isLoading)
                  ? RaisedButton(
                      color: Theme.of(context).accentColor,
                      onPressed: _login,
                      shape: RoundedRectangleBorder(
                          borderRadius: BorderRadius.all(Radius.circular(5))),
                      padding: EdgeInsets.symmetric(vertical: 15),
                      child: Row(
                        children: [
                          Expanded(
                            child: Text(
                              "Submit",
                              style: TextStyle(color: Colors.white),
                              textAlign: TextAlign.center,
                            ),
                          )
                        ],
                      ),
                    )
                  : CircularProgressIndicator(),
              SizedBox(
                height: 50,
              ),
              FlatButton(
                  onPressed: () {
                    Navigator.of(context).pushReplacement(MaterialPageRoute(
                        builder: (ct) => LandingPageScreen()));
                  },
                  child: Text("Skip login"))
            ],
          ),
        ),
      ),
    );
  }

  void _login() async {
    try {
      setState(() {
        isLoading = true;
      });
      var model = await Repository()
          .login(selectedType, _emailController.text, _passwordController.text);
      setState(() {
        isLoading = false;
      });
      SessionManager.isLoggedIn = true;
      SessionManager.userType = model.type;
      SessionManager.name = model.name;
      SessionManager.userId = model.id;
      SessionManager.token = model.token;
      Navigator.of(context)
          .pushReplacement(MaterialPageRoute(builder: (ct) => LandingPageScreen()));
    } catch (err) {
      setState(() {
        isLoading = false;
      });
      _scaffoldKey.currentState.showSnackBar(SnackBar(content: Text("$err")));
    }
  }
}
