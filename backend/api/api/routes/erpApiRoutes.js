'use strict';

module.exports = function(app) {

  app.get('/:id', (req, res) => {
    console.log(req.params.id);
    req.app.get('db').eosram.find({
      'id <=': req.params.id
    }, {
      order: [{field: 'dt', direction: 'desc'}]
    }).then(items => {
      res.json(items);
    });
  });

};
